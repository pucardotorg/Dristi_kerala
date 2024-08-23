package digit.web.models;

import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.pucar.dristi.web.models.Email;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class EmailRequest {
    private RequestInfo requestInfo;

    private Email email;
}