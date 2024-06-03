package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.models.Address;
import org.springframework.validation.annotation.Validated;

@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-05-29T13:38:04.562296+05:30[Asia/Calcutta]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RespondentDetails {

    @JsonProperty("name")
    @Pattern(regexp = "^[a-zA-Z]{1,100}$", message = "Name must be up to 100 alphabets with no numbers")
    private String name;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("gender")
    @NotNull
    private String gender;

    @JsonProperty("email")
    @Email(message = "Email should be valid")
    private String email;

    @JsonProperty("phone")
    @Pattern(regexp = "^\\+?[0-9\\-() ]+$", message = "Phone must be a valid phone number")
    private String phone;

    @JsonProperty("address")
    @Valid
    private Address address;

    @JsonProperty("relativeName")
    private String relativeName;

    @JsonProperty("relationWithRelative")
    private String relationWithRelative;
}
