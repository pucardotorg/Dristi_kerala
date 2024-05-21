package digit.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.Configuration;
import digit.config.ServiceConstants;
import digit.helper.DefaultMasterDataHelper;
import digit.kafka.Producer;
import digit.repository.ReScheduleRequestRepository;
import digit.web.models.*;
import digit.web.models.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HearingScheduler {

    @Autowired
    private Producer producer;

    @Autowired
    private ReScheduleRequestRepository repository;

    @Autowired
    private HearingService hearingService;

    @Autowired
    private DefaultMasterDataHelper helper;

    @Autowired
    private ServiceConstants serviceConstants;


    public void scheduleHearingForApprovalStatus(ReScheduleHearingRequest reScheduleHearingsRequest) {

        List<ReScheduleHearing> hearingsNeedToBeSchedule = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        HashMap<String, LocalDate> dateMap = new HashMap<>();
        List<ReScheduleHearing> blockedHearings = new ArrayList<>();
        for (ReScheduleHearing element : reScheduleHearingsRequest.getReScheduleHearing()) {
            if (Objects.equals(element.getWorkflow().getAction(), "SCHEDULE")) {
                hearingsNeedToBeSchedule.add(element);
                ids.add(element.getHearingBookingId());
                dateMap.put(element.getHearingBookingId(), element.getScheduleDate());
            }
            if (Objects.equals(element.getWorkflow().getAction(), "APPROVE")) {
                blockedHearings.add(element);

            }
        }

        ReScheduleHearingRequest request = ReScheduleHearingRequest.builder().reScheduleHearing(blockedHearings)
                .requestInfo(reScheduleHearingsRequest.getRequestInfo()).build();

        if (!blockedHearings.isEmpty()) producer.push("schedule-hearing-to-block-calendar", request);

        if (!hearingsNeedToBeSchedule.isEmpty()) {

            List<ScheduleHearing> hearings = hearingService.search(HearingSearchRequest.builder().criteria(HearingSearchCriteria.builder()
                            .hearingIds(ids).build())
                    .build());
            for (ScheduleHearing hearing : hearings) {
                hearing.setStatus(Status.SCHEDULED);
                hearing.setDate(dateMap.get(hearing.getHearingBookingId()));
            }

            List<MdmsSlot> defaultSlots = helper.getDataFromMDMS(MdmsSlot.class, serviceConstants.DEFAULT_SLOTTING_MASTER_NAME);

            List<MdmsHearing> defaultHearings = helper.getDataFromMDMS(MdmsHearing.class, serviceConstants.DEFAULT_HEARING_MASTER_NAME);
            Map<String, MdmsHearing> hearingTypeMap = defaultHearings.stream().collect(Collectors.toMap(
                    MdmsHearing::getHearingType,
                    obj -> obj
            ));

            ScheduleHearingRequest updateRequest = ScheduleHearingRequest.builder().hearing(hearings)
                    .requestInfo(reScheduleHearingsRequest.getRequestInfo()).build();
            hearingService.updateBulk(updateRequest, defaultSlots, hearingTypeMap);

        }


    }





}
