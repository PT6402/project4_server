package fpt.aptech.project4_server.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.repository.auth.TokenRepo;
import fpt.aptech.project4_server.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

  private final TokenRepo tokeRepo;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    try {
      var refreshToken = CookieUtils.getCookie(request, "refreshToken");
      if (!refreshToken.isPresent()) {
        throw new Exception("refreshToken is not found cookie");
      }
      var token = tokeRepo.findByRefreshToken(refreshToken.get().getValue());
      if (!token.isPresent()) {
        throw new Exception("refreshToken is not found database");
      }
      tokeRepo.deleteById(token.get().getId());
      CookieUtils.deleteCookie(request, response, "refreshToken");
      SecurityContextHolder.clearContext();
    } catch (Exception e) {
      log.error("error logout -{}", e.getMessage());
    }
  }

}
