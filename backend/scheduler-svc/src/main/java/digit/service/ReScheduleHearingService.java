package digit.service;


import digit.config.Configuration;
import digit.config.ServiceConstants;
import digit.enrichment.ReScheduleRequestEnrichment;
import digit.helper.DefaultMasterDataHelper;
import digit.kafka.Producer;
import digit.repository.ReScheduleRequestRepository;
import digit.validator.ReScheduleRequestValidator;
import digit.web.models.*;
import digit.web.models.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReScheduleHearingService {


    private final Configuration config;
    private ReScheduleRequestRepository repository;
    private ReScheduleRequestValidator validator;
    private ReScheduleRequestEnrichment enrichment;
    private Producer producer;
    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private HearingService hearingService;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private HearingScheduler hearingScheduler;

    @Autowired
    private ServiceConstants serviceConstants;

    @Autowired
    private DefaultMasterDataHelper helper;


    @Autowired
    public ReScheduleHearingService(ReScheduleRequestRepository repository, ReScheduleRequestValidator validator, ReScheduleRequestEnrichment enrichment, Producer producer, Configuration config) {
        this.repository = repository;
        this.validator = validator;
        this.enrichment = enrichment;
        this.producer = producer;
        this.config = config;
    }

    public List<ReScheduleHearing> create(ReScheduleHearingRequest reScheduleHearingsRequest) {
        List<ReScheduleHearing> reScheduleHearing = reScheduleHearingsRequest.getReScheduleHearing();

        validator.validateRescheduleRequest(reScheduleHearingsRequest);

        enrichment.enrichRescheduleRequest(reScheduleHearingsRequest);

        workflowService.updateWorkflowStatus(reScheduleHearingsRequest);

        producer.push(config.getRescheduleRequestCreateTopic(), reScheduleHearing);

        return reScheduleHearing;

    }

    public List<ReScheduleHearing> update(ReScheduleHearingRequest reScheduleHearingsRequest) {

        List<ReScheduleHearing> existingReScheduleHearingsReq = validator.validateExistingApplication(reScheduleHearingsRequest);

        enrichment.enrichRequestOnUpdate(reScheduleHearingsRequest, existingReScheduleHearingsReq);

        workflowService.updateWorkflowStatus(reScheduleHearingsRequest);

        // here if its approved we need to calculate date
        // then schedule dummy hearings for judge to block the calendar
        hearingScheduler.scheduleHearingForApprovalStatus(reScheduleHearingsRequest);

        producer.push(config.getUpdateRescheduleRequestTopic(), reScheduleHearingsRequest.getReScheduleHearing());

        return reScheduleHearingsRequest.getReScheduleHearing();

    }

    public List<ReScheduleHearing> search(ReScheduleHearingReqSearchRequest request, Integer limit, Integer offset) {
        return repository.getReScheduleRequest(request.getCriteria(), limit, offset);
    }

    public List<ReScheduleHearing> bulkReschedule(BulkReScheduleHearingRequest request) {

        validator.validateBulkRescheduleRequest(request);

        List<MdmsSlot> defaultSlots = helper.getDataFromMDMS(MdmsSlot.class, serviceConstants.DEFAULT_SLOTTING_MASTER_NAME);

        double totalHrs = defaultSlots.stream().reduce(0.0, (total, slot) -> total + slot.getSlotDuration() / 60.0, Double::sum);
        List<MdmsHearing> defaultHearings = helper.getDataFromMDMS(MdmsHearing.class, serviceConstants.DEFAULT_HEARING_MASTER_NAME);
        Map<String, MdmsHearing> hearingTypeMap = defaultHearings.stream().collect(Collectors.toMap(
                MdmsHearing::getHearingType,
                obj -> obj
        ));
        BulkReschedulingOfHearings bulkRescheduling = request.getBulkRescheduling();

        String tenantId = request.getRequestInfo().getUserInfo().getTenantId();
        String judgeId = bulkRescheduling.getJudgeId();
        LocalDateTime endTime = bulkRescheduling.getEndTime();
        LocalDateTime startTime = bulkRescheduling.getStartTime();
        LocalDate fromDate = bulkRescheduling.getScheduleAfter();

        HearingSearchCriteria criteria = HearingSearchCriteria.builder().judgeId(judgeId).startDateTime(startTime).endDateTime(endTime).tenantId(tenantId)
                .status(Arrays.asList(Status.SCHEDULED, Status.BLOCKED)).build();

        List<ScheduleHearing> hearings = hearingService.search(HearingSearchRequest.builder().requestInfo(request.getRequestInfo()).criteria(criteria).build(), null, null);

        if (CollectionUtils.isEmpty(hearings)) {
            return new ArrayList<>();
        }

        List<ReScheduleHearing> reScheduleHearings = createReschedulingRequest(hearings, request.getRequestInfo().getUserInfo().getUuid());
        // create rescheduling req in db
        create(ReScheduleHearingRequest.builder().reScheduleHearing(reScheduleHearings).requestInfo(request.getRequestInfo()).build());

        //get available date
        List<AvailabilityDTO> availability = calendarService.getJudgeAvailability(
                JudgeAvailabilitySearchRequest.builder().requestInfo(request.getRequestInfo())
                        .criteria(JudgeAvailabilitySearchCriteria.builder()
                                .judgeId(judgeId)
                                .fromDate(fromDate)
                                .courtId("0001")
                                .numberOfSuggestedDays(hearings.size()+10)
                                .tenantId(tenantId)// need to configure some where
                                .build()).build()
        );

        // assign slots and push for schedule hearing
        int index = 0;
        Double requiredSlot = null;
        for (ScheduleHearing hearing : hearings) {
            String eventType = hearing.getEventType().toString();

            MdmsHearing hearingType = hearingTypeMap.get(eventType);
            requiredSlot = hearingType.getHearingTime() / 60.00;

            Double occupiedBandwidth = availability.get(index).getOccupiedBandwidth();
            if (totalHrs - occupiedBandwidth > requiredSlot) {  // need to configure
                hearing.setDate(LocalDate.parse(availability.get(index).getDate()));
                availability.get(index).setOccupiedBandwidth(occupiedBandwidth + requiredSlot);  // need to configure
            } else {
                hearing.setDate(LocalDate.parse(availability.get(++index).getDate()));
                availability.get(index).setOccupiedBandwidth(availability.get(index).getOccupiedBandwidth() + requiredSlot);  // need to configure
            }
        }
        // try to make it async
        // updated hearing in hearing table
        hearingService.updateBulk(ScheduleHearingRequest.builder()
                .hearing(hearings).requestInfo(request.getRequestInfo()).build(),defaultSlots,hearingTypeMap);


        return reScheduleHearings;
    }

    private List<ReScheduleHearing> createReschedulingRequest(List<ScheduleHearing> hearings, String requesterId) {
        List<ReScheduleHearing> resultList = new ArrayList<>();

        Workflow workflow = Workflow.builder().action("AUTO_SCHEDULE").assignees(new ArrayList<>()).comment("bulk reschedule by :" + requesterId).build();
        for (ScheduleHearing hearing : hearings) {

            ReScheduleHearing reScheduleHearingReq = ReScheduleHearing.builder()
                    .hearingBookingId(hearing.getHearingBookingId())
                    .judgeId(hearing.getJudgeId())
                    .caseId(hearing.getCaseId())
                    .tenantId(hearing.getTenantId())
                    .requesterId(requesterId)
                    .workflow(workflow)
                    .actionComment("AUTO SCHEDULE BY JUDGE")
                    .reason("reschedule by judge")
                    .build();

            resultList.add(reScheduleHearingReq);

        }
        return resultList;
    }
}
