package fpt.aptech.project4_server.entities.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    ADMIN, USER;

    public List<SimpleGrantedAuthority> getAuthrities() {

        var authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
