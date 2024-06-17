package fpt.aptech.project4_server.service;

import org.springframework.http.ResponseEntity;

import fpt.aptech.project4_server.dto.authenticate.AuthReq;
import fpt.aptech.project4_server.dto.authenticate.RegisterReq;
import fpt.aptech.project4_server.dto.authenticate.ResetPasswordReq;

public interface AuthService {
    ResponseEntity<?> register(RegisterReq req);

    ResponseEntity<?> authenticate(AuthReq req);

    ResponseEntity<?> checkTypeLogin(String email);

    ResponseEntity<?> forgotPassword(String email);

    ResponseEntity<?> resetPasswordUser(ResetPasswordReq req);

    ResponseEntity<?> checkCodeReset(ResetPasswordReq req);
}
