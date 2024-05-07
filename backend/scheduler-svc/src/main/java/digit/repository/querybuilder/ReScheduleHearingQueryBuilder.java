package digit.repository.querybuilder;


import digit.helper.QueryBuilderHelper;
import digit.web.models.ReScheduleHearingReqSearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@Slf4j
public class ReScheduleHearingQueryBuilder {


    @Autowired
    private QueryBuilderHelper helper;

    private final String BASE_APPLICATION_QUERY = "SELECT hbr.rescheduled_request_id, hbr.hearing_booking_id, hbr.tenant_id, hbr.judge_id, hbr.case_id,hbr.requester_id,hbr.reason,hbr.status,hbr.action_comment,hbr.documents, hbr.created_by,hbr.last_modified_by,hbr.created_time,hbr.last_modified_time, hbr.row_version  ";

    private static final String FROM_TABLES = " FROM hearing_booking_reschedule_request hbr ";

    private final String ORDER_BY = " ORDER BY ";

    private final String LIMIT_OFFSET = " LIMIT ? OFFSET ?";

    public String getReScheduleRequestQuery(ReScheduleHearingReqSearchCriteria searchCriteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_APPLICATION_QUERY);
        query.append(FROM_TABLES);

        if (!CollectionUtils.isEmpty(searchCriteria.getRescheduledRequestId())) {
            helper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hbr.rescheduled_request_id IN ( ").append(helper.createQuery(searchCriteria.getRescheduledRequestId())).append(" ) ");
            helper.addToPreparedStatement(preparedStmtList, searchCriteria.getRescheduledRequestId());
        }

        if (!ObjectUtils.isEmpty(searchCriteria.getTenantId())) {
            helper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hbr.tenant_id = ? ");
            preparedStmtList.add(searchCriteria.getTenantId());
        }

        if (!ObjectUtils.isEmpty(searchCriteria.getJudgeId())) {
            helper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hbr.judge_id = ? ");
            preparedStmtList.add(searchCriteria.getJudgeId());
        }

        if (!ObjectUtils.isEmpty(searchCriteria.getJudgeId())) {
            helper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hbr.case_id = ? ");
            preparedStmtList.add(searchCriteria.getJudgeId());
        }

        if (!ObjectUtils.isEmpty(searchCriteria.getHearingBookingId())) {
            helper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hbr.hearing_booking_id = ? ");
            preparedStmtList.add(searchCriteria.getHearingBookingId());
        }
        if (!ObjectUtils.isEmpty(searchCriteria.getRequesterId())) {
            helper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hbr.requester_id = ? ");
            preparedStmtList.add(searchCriteria.getRequesterId());
        }
        if (!ObjectUtils.isEmpty(searchCriteria.getStatus())) {
            helper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hbr.status = ? ");
            preparedStmtList.add(searchCriteria.getStatus());
        }


        return query.toString();
    }
}
