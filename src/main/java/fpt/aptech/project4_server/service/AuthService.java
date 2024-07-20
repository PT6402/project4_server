package fpt.aptech.project4_server.service;

import org.springframework.http.ResponseEntity;

import fpt.aptech.project4_server.dto.authenticate.AuthReq;
import fpt.aptech.project4_server.dto.authenticate.RegisterReq;
import fpt.aptech.project4_server.dto.authenticate.ResetPasswordReq;
import fpt.aptech.project4_server.entities.user.TypeDevice;
import fpt.aptech.project4_server.util.ResultDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    ResponseEntity<ResultDto<?>> register(RegisterReq req);

    ResponseEntity<ResultDto<?>> authenticate(AuthReq req);

    ResponseEntity<ResultDto<?>> checkTypeLogin(String email);

    ResponseEntity<ResultDto<?>> forgotPassword(String email);

    ResponseEntity<ResultDto<?>> resetPasswordUser(ResetPasswordReq req);

    ResponseEntity<ResultDto<?>> checkCodeReset(ResetPasswordReq req);

    ResponseEntity<ResultDto<?>> refreshToken(HttpServletRequest request, HttpServletResponse response,
            TypeDevice typeDevice);

    ResponseEntity<ResultDto<?>> reloadPage(HttpServletRequest request);

}
