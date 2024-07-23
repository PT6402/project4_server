package fpt.aptech.project4_server.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import fpt.aptech.project4_server.entities.auth.Role;
import fpt.aptech.project4_server.entities.auth.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserGlobal implements OAuth2User, UserDetails {
  private Integer id;
  private String email;
  private String password;
  private String name;
  private Collection<? extends GrantedAuthority> authorities;
  private Map<String, Object> attributes;

  public static UserGlobal createUser(User user) {
    List<GrantedAuthority> authorities = Collections
        .singletonList(new SimpleGrantedAuthority(Role.user.toString()));

    return UserGlobal.builder()
        .id(user.getId())
        .password(user.getPassword())
        .email(user.getEmail())
        .authorities(authorities)
        .build();
  }

  public static UserGlobal createAdmin(User user) {
    List<GrantedAuthority> authorities = Collections
        .singletonList(new SimpleGrantedAuthority(Role.admin.toString()));

    return UserGlobal.builder()
        .id(user.getId())
        .password(user.getPassword())
        .email(user.getPassword())
        .authorities(authorities)
        .build();
  }

  public static UserGlobal createUser(User user, Map<String, Object> attributes) {
    UserGlobal userGlobal = createUser(user);
    userGlobal.setAttributes(attributes);
    return userGlobal;
  }

  public static UserGlobal createAdmin(User user, Map<String, Object> attributes) {
    UserGlobal userGlobal = createAdmin(user);
    userGlobal.setAttributes(attributes);
    return userGlobal;
  }

  @Override
  public String getUsername() {
    return email;
  }

}
