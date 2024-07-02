package fpt.aptech.project4_server.service;

import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String generateResetPass(UserDetails userDetails);

    boolean inValidToken(String token, UserDetails userDetails);

    String extractUsername(String token);

    Date extractExpiration(String token);
}
