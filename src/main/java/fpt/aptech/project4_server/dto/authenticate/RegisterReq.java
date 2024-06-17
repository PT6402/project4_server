package fpt.aptech.project4_server.dto.authenticate;

import com.fasterxml.jackson.annotation.JsonProperty;

import fpt.aptech.project4_server.entities.user.TypeDevice;
import fpt.aptech.project4_server.entities.user.TypeLogin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterReq {
    private String fullname;
    private String email;
    private String password;

    @JsonProperty(value = "type_device")
    private TypeDevice typeDevice;

    @JsonProperty(value = "type_login")
    private TypeLogin typeLogin;
}
