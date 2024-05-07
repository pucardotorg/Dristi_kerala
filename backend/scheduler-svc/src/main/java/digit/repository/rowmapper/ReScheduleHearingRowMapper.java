package digit.repository.rowmapper;

import digit.models.coremodels.AuditDetails;
import digit.web.models.ReScheduleHearing;
import digit.web.models.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
@Slf4j
public class ReScheduleHearingRowMapper implements RowMapper<ReScheduleHearing> {

    @Override
    public ReScheduleHearing mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        ReScheduleHearing reScheduleHearing = ReScheduleHearing.builder()
                .rescheduledRequestId(resultSet.getString("rescheduled_request_id"))
                .hearingBookingId(resultSet.getString("hearing_booking_id"))
                .tenantId(resultSet.getString("tenant_id"))
                .judgeId(resultSet.getString("judge_id"))
                .caseId(resultSet.getString("case_id"))
                .requesterId(resultSet.getString("requester_id"))
                .reason(resultSet.getString("reason"))
                .status(Status.valueOf(resultSet.getString("status")))
                .actionComment(resultSet.getString("action_comment"))
                .auditDetails(AuditDetails.builder()
                        .createdBy(resultSet.getString("created_by"))
                        .createdTime(resultSet.getLong("created_time"))
                        .lastModifiedBy(resultSet.getString("last_modified_by"))
                        .lastModifiedTime(resultSet.getLong("last_modified_time"))
                        .build())
                .rowVersion(resultSet.getInt("row_version")).build();
        return reScheduleHearing;
    }
}
