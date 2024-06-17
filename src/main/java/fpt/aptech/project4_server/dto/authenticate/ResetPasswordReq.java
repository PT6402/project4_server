package fpt.aptech.project4_server.dto.authenticate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResetPasswordReq {
    private String email;
    private String code;

    @JsonProperty(value = "new_password")
    private String newPassword;

    @JsonProperty(value = "access_token")
    private String accessToken;
}
