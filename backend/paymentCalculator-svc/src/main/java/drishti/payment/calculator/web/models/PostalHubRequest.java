package drishti.payment.calculator.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * PostalHubRequest
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-06-10T14:05:42.847785340+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostalHubRequest {
    @JsonProperty("RequestInfo")

    @Valid
    private RequestInfo requestInfo = null;

    @JsonProperty("hubs")
    @Valid
    private List<Hub> hubs = null;


    public PostalHubRequest addHubsItem(Hub hubsItem) {
        if (this.hubs == null) {
            this.hubs = new ArrayList<>();
        }
        this.hubs.add(hubsItem);
        return this;
    }

}
