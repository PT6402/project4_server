package fpt.aptech.project4_server.service.auth_google;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import fpt.aptech.project4_server.util.ResultDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthGGService {
  void loginWeb(Authentication authentication, HttpServletResponse httpResponse, HttpServletRequest httpRequest);

  ResponseEntity<ResultDto<?>> loginMobile(String token, HttpServletResponse httpResponse,
      HttpServletRequest httpRequest);
}
