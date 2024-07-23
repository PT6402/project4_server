package fpt.aptech.project4_server.dto.user;

import fpt.aptech.project4_server.entities.auth.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRes {
  private String email;
  private Role role;
}
