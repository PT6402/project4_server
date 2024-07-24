package fpt.aptech.project4_server.service.jwt;

import org.springframework.security.core.Authentication;

public interface JwtService {

    String generateAccessToken(Authentication authentication);

    String generateRefreshToken(Authentication authentication);

    String generateResetPassword(Authentication authentication);

    String generatePaymentToken(String email);

    Integer getUserIdByToken(String token);
}
