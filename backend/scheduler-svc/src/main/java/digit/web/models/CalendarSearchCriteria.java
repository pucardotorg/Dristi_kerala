package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import digit.web.models.enums.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarSearchCriteria implements SearchCriteria {

    @JsonProperty("tenantId")
    private String tenantId;            // required field

    @JsonProperty("judgeId")
    private String judgeId;             // required field

    @JsonProperty("courtId")
    private String courtId;             // required field

    @JsonProperty("fromDate")
    private LocalDate fromDate;

    @JsonProperty("toDate")
    private LocalDate toDate;

    @JsonProperty("periodType")
    private PeriodType periodType;


}
