package fpt.aptech.project4_server.service.auth;

import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.config.AppProperties;
import fpt.aptech.project4_server.entities.auth.AuthProvider;
import fpt.aptech.project4_server.entities.auth.Role;
import fpt.aptech.project4_server.entities.auth.Token;
import fpt.aptech.project4_server.entities.auth.User;
import fpt.aptech.project4_server.repository.auth.TokenRepo;
import fpt.aptech.project4_server.repository.auth.UserRepo;
import fpt.aptech.project4_server.security.CusUserDetailsService;
import fpt.aptech.project4_server.service.jwt.JwtService;
import fpt.aptech.project4_server.service.mail.MailService;
import fpt.aptech.project4_server.util.CookieUtils;
import fpt.aptech.project4_server.util.ResultDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AppProperties appProperties;

    private final UserRepo userRepo;
    private final TokenRepo tokeRepo;

    private final JwtService jwtService;
    private final MailService mailService;
    private final CusUserDetailsService customUserDetailsService;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public ResponseEntity<ResultDto<?>> register(String email, String password) {
        try {
            User checkExist = userRepo.findByEmail(email).orElse(null);

            if (checkExist != null) {
                throw new Exception("user already exist");
            }

            // create user
            User newUser = User.builder()
                    .email(email)
                    .role(Role.user)
                    .provider(AuthProvider.local)
                    .password(passwordEncoder.encode(password))
                    .build();

            userRepo.save(newUser);
            ResultDto<?> response = ResultDto.builder().message("register mail success").status(true).build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().status(false).message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Override
    public ResponseEntity<ResultDto<?>> authenticate(String email, String password, HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        try {
            User user = userRepo.findByEmail(email).orElse(null);
            if (user == null) {
                throw new Exception("user not found");
            }

            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            String accessToken = jwtService.generateAccessToken(auth);
            String refreshToken = jwtService.generateRefreshToken(auth);
            tokeRepo.save(Token.builder()
                    .ipAddress(httpRequest.getRemoteAddr())
                    .inforDevice(httpRequest.getHeader("User-Agent"))
                    .refreshToken(refreshToken).user(userRepo.findByEmail(auth.getName()).get()).build());
            CookieUtils.addCookie(httpResponse, "refreshToken",
                    refreshToken,
                    Integer.parseInt(appProperties.getCors().getMax_age_secs()));

            ResultDto<?> response = ResultDto.builder().message("login success").status(true).model(accessToken)
                    .build();
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            ResultDto<?> response = ResultDto.builder().message("password wrond").status(false).build();
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
            return ResponseEntity.badRequest().body(response);
        }

    }

    @Override
    public ResponseEntity<ResultDto<?>> checkTypeLogin(String email) {
        try {
            var user = userRepo.findByEmail(email).orElse(null);
            if (user == null) {
                throw new Exception("user not found");
            }
            ResultDto<?> response = ResultDto.builder().model(user.getProvider()).status(true).build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Override
    public ResponseEntity<ResultDto<?>> forgotPassword(String email, HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        try {
            var user = userRepo.findByEmail(email).orElse(null);
            if (user == null) {
                throw new Exception("user not found");
            }
            if (user.getProvider().equals(AuthProvider.google)) {
                throw new Exception("invalid provider");
            }

            // handle random code
            Random random = new Random();
            String randomNumber = String.format("%04d", random.nextInt(10000));
            mailService.sendResetPassword(email, randomNumber);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());

            // handle token
            String tokenReset = jwtService.generateResetPassword(auth);
            tokeRepo.save(Token.builder()
                    .ipAddress(httpRequest.getRemoteAddr())
                    .inforDevice(httpRequest.getHeader("User-Agent"))
                    .refreshToken(tokenReset + randomNumber).user(userRepo.findByEmail(auth.getName()).get()).build());
            CookieUtils.addCookie(httpResponse, "token_reset_pass",
                    tokenReset,
                    (int) appProperties.getAuth().getReset_pass_token_expired());

            ResultDto<?> response = ResultDto.builder().message("send mail success").status(true)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Override
    public ResponseEntity<ResultDto<?>> checkCodeReset(String code, HttpServletRequest httpRequest) {
        try {
            var cookieResetPass = CookieUtils.getCookie(httpRequest, "token_reset_pass");
            if (!cookieResetPass.isPresent()) {
                throw new Exception("token reset pass not found");
            }
            var tokenReset = tokeRepo.findByRefreshToken(cookieResetPass.get().getValue() + code);
            if (!tokenReset.isPresent()) {
                throw new Exception("token reset not found");
            }

            var userId = jwtService.getUserIdByToken(cookieResetPass.get().getValue());
            if (!userRepo.findById(userId).isPresent()) {
                throw new Exception("token invalid");
            }
            ResultDto<?> response = ResultDto.builder().status(true).build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Override
    public ResponseEntity<ResultDto<?>> resetPasswordUser(String code, String newPassword,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        try {
            var cookieResetPass = CookieUtils.getCookie(httpRequest, "token_reset_pass");
            if (!cookieResetPass.isPresent()) {
                throw new Exception("token reset pass not found");
            }
            var tokenReset = tokeRepo.findByRefreshToken(cookieResetPass.get().getValue() + code);
            if (!tokenReset.isPresent()) {
                throw new Exception("token reset not found");
            }

            var userId = jwtService.getUserIdByToken(cookieResetPass.get().getValue());
            var user = userRepo.findById(userId).orElse(null);
            if (user == null) {
                throw new Exception("token invalid");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);
            tokeRepo.deleteById(tokenReset.get().getId());
            CookieUtils.deleteCookie(httpRequest, httpResponse, "token_reset_pass");
            ResultDto<?> response = ResultDto.builder().message("reset password success!").status(true).build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Override
    public ResponseEntity<ResultDto<?>> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        try {
            var refreshToken = CookieUtils.getCookie(request, "refreshToken");
            if (!refreshToken.isPresent()) {
                throw new Exception("refreshToken is not found cookie");
            }
            var token = tokeRepo.findByRefreshToken(refreshToken.get().getValue());
            if (!token.isPresent()) {
                throw new Exception("refreshToken is not found database");
            }
            if (!(token.get().getIpAddress().equals(request.getRemoteAddr()))
                    && !(token.get().getInforDevice().equals(request.getHeader("User-Agent")))) {
                throw new Exception("device invalid");
            }

            var user = token.get().getUser();
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());
            String newAccessToken = jwtService.generateAccessToken(auth);

            ResultDto<?> result = ResultDto.builder().message("refresh token success").model(
                    newAccessToken)
                    .status(true).build();

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            ResultDto<?> result = ResultDto.builder().message(e.getMessage())
                    .status(false).build();
            return ResponseEntity.badRequest().body(result);
        }
    }

}