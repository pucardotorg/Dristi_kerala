package digit.web.models;

import digit.web.models.Email;
import org.egov.common.contract.request.RequestInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class EmailRequest {
    private RequestInfo requestInfo;

    private Email email;
}