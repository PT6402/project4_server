package fpt.aptech.project4_server.dto.user;

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
public class UserAdminDTO {
    private String name;
    private String email;
    private TypeLogin typeLogin;
    private TypeRole role;
}
