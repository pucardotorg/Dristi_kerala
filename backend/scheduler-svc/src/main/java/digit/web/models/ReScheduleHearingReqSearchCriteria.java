package digit.web.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import digit.web.models.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReScheduleHearingReqSearchCriteria {


    @JsonProperty("rescheduledRequestId")
    private List<String> rescheduledRequestId;

    @JsonProperty("hearingBookingId")
    private String hearingBookingId;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("judgeId")
    private String judgeId;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("requesterId")
    private String requesterId;

    @JsonProperty("status")
    private Status status;

}
