package fpt.aptech.project4_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fpt.aptech.project4_server.dto.authenticate.AuthReq;
import fpt.aptech.project4_server.dto.authenticate.RegisterReq;
import fpt.aptech.project4_server.dto.authenticate.ResetPasswordReq;
import fpt.aptech.project4_server.entities.user.TypeDevice;
import fpt.aptech.project4_server.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    ResponseEntity<?> authenticate(@RequestBody AuthReq req) {
        return authService.authenticate(req);
    }

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody RegisterReq req) {
        return authService.register(req);
    }

    @GetMapping("/check-type-login/{email}")
    public ResponseEntity<?> checkTypeLogin(@PathVariable String email) {
        return authService.checkTypeLogin(email);
    }

    @GetMapping("/forgot-password/{email}")
    public ResponseEntity<?> forgotPassword(@PathVariable String email) {
        return authService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordReq req) {
        return authService.resetPasswordUser(req);
    }

    @PostMapping("/check-code-reset")
    public ResponseEntity<?> checkCodeReset(@RequestBody ResetPasswordReq req) {
        return authService.checkCodeReset(req);
    }

    @GetMapping("/refresh-token/{typeDevice}")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response,
            @PathVariable TypeDevice typeDevice) {
        return authService.refreshToken(request, response, typeDevice);
    }

    @GetMapping("/reload")
    public ResponseEntity<?> reloadPage(HttpServletRequest request) {
        return authService.reloadPage(request);
    }

}
