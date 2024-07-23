package fpt.aptech.project4_server.service.auth;

import org.springframework.http.ResponseEntity;
import fpt.aptech.project4_server.util.ResultDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    // handle authenticate
    ResponseEntity<ResultDto<?>> register(String email, String password);

    ResponseEntity<ResultDto<?>> authenticate(String email, String password, HttpServletRequest httpRequest,
            HttpServletResponse response);

    ResponseEntity<ResultDto<?>> checkTypeLogin(String email);

    // handle password

    ResponseEntity<ResultDto<?>> forgotPassword(String email, HttpServletRequest httpRequest,
            HttpServletResponse httpResponse);

    ResponseEntity<ResultDto<?>> checkCodeReset(String code, HttpServletRequest httpRequest);

    ResponseEntity<ResultDto<?>> resetPasswordUser(String code, String newPassword, HttpServletRequest httpRequest,
            HttpServletResponse httpResponse);

    // token
    ResponseEntity<ResultDto<?>> refreshToken(HttpServletRequest request, HttpServletResponse response);
}