package fpt.aptech.project4_server.controller.auth;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fpt.aptech.project4_server.service.auth.AuthService;
import fpt.aptech.project4_server.service.auth_google.AuthGGService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthGGService gg_service;

    @PostMapping("/loginGG")
    public ResponseEntity<?> postMethodName(@RequestParam String token, HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        return gg_service.loginMobile(token, httpResponse, httpRequest);
    }

    @PostMapping("/login")
    ResponseEntity<?> authenticate(@RequestBody Map<String, String> req, HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        return authService.authenticate(req.get("email"), req.get("password"), httpRequest, httpResponse);
    }

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody Map<String, String> req) {
        return authService.register(req.get("email"), req.get("password"),req.get("name"));
    }

    @GetMapping("/check-type-login/{email}")
    public ResponseEntity<?> checkTypeLogin(@PathVariable String email) {
        return authService.checkTypeLogin(email);
    }

    @GetMapping("/forgot-password/{email}")
    public ResponseEntity<?> forgotPassword(@PathVariable String email, HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        return authService.forgotPassword(email, httpRequest, httpResponse);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> req, HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        return authService.resetPasswordUser(req.get("code"), req.get("newPassword"), httpRequest, httpResponse);
    }

    @PostMapping("/check-code-reset")
    public ResponseEntity<?> checkCodeReset(@RequestParam String code, HttpServletRequest httpRequest) {
        return authService.checkCodeReset(code, httpRequest);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshToken(request, response);
    }
}
