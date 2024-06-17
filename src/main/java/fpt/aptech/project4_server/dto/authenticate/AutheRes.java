package fpt.aptech.project4_server.dto.authenticate;

import com.fasterxml.jackson.annotation.JsonProperty;

import fpt.aptech.project4_server.entities.user.TypeLogin;
import fpt.aptech.project4_server.entities.user.TypeRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AutheRes {
    private String fullname;
    private String email;
    private TypeRole role;

    @JsonProperty(value = "type_login")
    private TypeLogin typeLogin;

    @JsonProperty(value = "access_token")
    private String accessToken;

    @JsonProperty(value = "refresh_token")
    private String refreshToken;

}
