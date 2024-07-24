package fpt.aptech.project4_server.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fpt.aptech.project4_server.repository.auth.UserRepo;

@Slf4j
@Service
@RequiredArgsConstructor
public class CusUserDetailsService implements UserDetailsService {
  private final UserRepo userRepo;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    try {

      var user = userRepo.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("user not found by email: " + email));
      return UserGlobal.createUser(user);
    } catch (Exception e) {
      log.error(e.getMessage());
      return null;
    }
  }

  @Transactional
  public UserDetails loadUserById(int userId) {
    try {
      var user = userRepo.findById(userId)
          .orElseThrow(() -> new Exception("user not found by id: " + userId));
      return UserGlobal.createUser(user);
    } catch (Exception e) {
      log.error(e.getMessage());
      return null;
    }
  }
}
