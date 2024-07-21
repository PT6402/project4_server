package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.entities.user.UserDetail;

import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    String generateAccessToken(UserDetails userDetails);

    String generatePaymentToken(UserDetail userDetail);

    String generateRefreshToken(UserDetails userDetails);

    String generateResetPass(UserDetails userDetails);

    boolean inValidToken(String token, UserDetails userDetails);

    String extractUsername(String token);

    Date extractExpiration(String token);
}
