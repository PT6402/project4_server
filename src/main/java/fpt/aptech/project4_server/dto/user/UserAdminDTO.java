package fpt.aptech.project4_server.dto.user;

import fpt.aptech.project4_server.entities.auth.AuthProvider;
import fpt.aptech.project4_server.entities.auth.Role;
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
    private AuthProvider typeLogin;
    private Role role;
}
