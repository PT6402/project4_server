package fpt.aptech.project4_server.service;

import java.util.Date;
import java.util.Random;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fpt.aptech.project4_server.dto.authenticate.AuthReq;
import fpt.aptech.project4_server.dto.authenticate.AutheRes;
import fpt.aptech.project4_server.dto.authenticate.RegisterReq;
import fpt.aptech.project4_server.dto.authenticate.ResetPasswordReq;
import fpt.aptech.project4_server.entities.user.Token;
import fpt.aptech.project4_server.entities.user.TypeDevice;
import fpt.aptech.project4_server.entities.user.TypeLogin;
import fpt.aptech.project4_server.entities.user.TypeRole;
import fpt.aptech.project4_server.entities.user.User;
import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.repository.TokenRepo;
import fpt.aptech.project4_server.repository.UserDetailRepo;
import fpt.aptech.project4_server.repository.UserRepo;
import fpt.aptech.project4_server.util.ResultDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final UserRepo userRepo;
    private final TokenRepo tokenRepo;
    private final UserDetailRepo userDetailRepo;

    private final JwtService jwtService;
    private final MailService mailService;

    @Override
    public ResponseEntity<ResultDto<?>> register(RegisterReq req) {
        User checkExist = userRepo.findByEmail(req.getEmail()).orElse(null);

        if (checkExist != null) {
            ResultDto<?> response = ResultDto.builder().status(false).message("user exist").build();
            return new ResponseEntity<ResultDto<?>>(response, HttpStatus.CONFLICT);
        }

        // create user
        User newUser = User.builder()
                .email(req.getEmail())
                .role(TypeRole.USER)
                .build();

        if (req.getTypeLogin().equals(TypeLogin.GOOGLE)) {
            if (!isValidAccessTokenGG(req.getPassword())) {
                ResultDto<?> response = ResultDto.builder().message("invalid token").status(false).build();
                return new ResponseEntity<ResultDto<?>>(response, HttpStatus.BAD_REQUEST);
            } else {
                newUser.setTypeLogin(TypeLogin.GOOGLE);
                newUser.setPassword(null);
            }
        } else {
            newUser.setTypeLogin(TypeLogin.EMAIL);
            newUser.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        var userSaved = userRepo.save(newUser);

        // create user_detail
        UserDetail newUserDetail = UserDetail.builder()
                .user(userSaved)
                .fullname(req.getFullname())
                .avartar(null)
                .build();
        var newUserDetailSaved = userDetailRepo.save(newUserDetail);
        if (req.getTypeLogin().equals(TypeLogin.GOOGLE)) {
            // token
            String accessToken = jwtService.generateAccessToken(userSaved);
            String refreshToken = jwtService.generateRefreshToken(userSaved);

            var tokenUser = Token.builder()
                    .user(userSaved)
                    .token(accessToken)
                    .expired(false)
                    .revoked(false)
                    .typeDevice(req.getTypeDevice())
                    .isResetPassword(false)
                    .build();
            tokenRepo.save(tokenUser);

            var result = AutheRes.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .email(userSaved.getEmail())
                    .fullname(newUserDetailSaved.getFullname())
                    .role(userSaved.getRole())
                    .typeLogin(userSaved.getTypeLogin())
                    .build();
            ResultDto<?> response = ResultDto.builder().message("register gg success").status(true).model(result)
                    .build();
            return ResponseEntity.ok(response);
        }
        ResultDto<?> response = ResultDto.builder().message("register mail success").status(true).build();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResultDto<?>> authenticate(AuthReq req) {
        // check exist account
        User user = userRepo.findByEmail(req.getEmail()).orElse(null);
        if (user == null) {
            ResultDto<?> response = ResultDto.builder().message("user not found").status(false).build();
            return new ResponseEntity<ResultDto<?>>(response, HttpStatus.NOT_FOUND);
        }

        // check type login
        if (req.getTypeLogin().equals(TypeLogin.GOOGLE)) {
            if (!isValidAccessTokenGG(req.getPassword())) {
                ResultDto<?> response = ResultDto.builder().status(false).message("invalid token").build();
                return new ResponseEntity<ResultDto<?>>(response, HttpStatus.BAD_REQUEST);
            }

            // TODO: check email match email in google
        } else {
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
            } catch (AuthenticationException e) {
                ResultDto<?> response = ResultDto.builder().message("password wrond").status(false).build();
                return new ResponseEntity<ResultDto<?>>(response, HttpStatus.BAD_REQUEST);
            }
        }

        // token
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user, req.getTypeDevice());
        var tokenUser = Token.builder()
                .user(user)
                .token(accessToken)
                .expired(false)
                .revoked(false)
                .typeDevice(req.getTypeDevice())
                .isResetPassword(false)
                .build();
        tokenRepo.save(tokenUser);

        var result = AutheRes.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .typeLogin(user.getTypeLogin())
                .fullname(userDetailRepo.findByUser(user).orElse(null).getFullname())
                .role(user.getRole())
                .build();

        ResultDto<?> response = ResultDto.builder().message("login success").status(true).model(result).build();
        return ResponseEntity.ok(response);

    }

    @Override
    public ResponseEntity<ResultDto<?>> checkTypeLogin(String email) {
        var user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            ResultDto<?> response = ResultDto.builder().message("user not found").status(false).build();
            return new ResponseEntity<ResultDto<?>>(response, HttpStatus.NOT_FOUND);
        } else {
            ResultDto<?> response = ResultDto.builder().model(user.getTypeLogin()).status(true).build();
            return ResponseEntity.ok(response);
        }
    }

    @Override
    public ResponseEntity<ResultDto<?>> forgotPassword(String email) {

        try {
            var user = userRepo.findByEmail(email).orElse(null);

            if (user == null) {
                ResultDto<?> response = ResultDto.builder().message("user not found").status(false).build();
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            if (user.getTypeLogin().equals(TypeLogin.GOOGLE)) {
                ResultDto<?> response = ResultDto.builder().message("type invalid").status(false).build();
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Random random = new Random();
            int randomNumber = random.nextInt(10000);
            var checkSendMail = mailService.sendMail(email, "reset password",
                    "this is code to reset password: " + String.format("%04d", randomNumber));
            if (!checkSendMail) {
                ResultDto<?> response = ResultDto.builder().message("mail send fail!").status(false).build();
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                String accessToken = jwtService.generateResetPass(user);
                var token = Token.builder()
                        .user(user)
                        .token(accessToken + "|" + String.format("%04d",
                                randomNumber))
                        .expired(false)
                        .revoked(false)
                        .isResetPassword(true)
                        .build();
                tokenRepo.save(token);
                ResultDto<?> response = ResultDto.builder().message("send mail success").status(true).model(accessToken)
                        .build();
                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().message("mail send fail!").status(false).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ResultDto<?>> resetPasswordUser(ResetPasswordReq req) {
        try {
            if (jwtService.extractExpiration(req.getAccessToken()).before(new Date())) {
                ResultDto<?> response = ResultDto.builder().message("token expired").status(false).build();
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (NullPointerException e) {
            ResultDto<?> response = ResultDto.builder().message("token expired").status(false).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().message("send mail fail").status(false).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        String email = jwtService.extractUsername(req.getAccessToken());
        if (!email.equals(req.getEmail())) {
            ResultDto<?> response = ResultDto.builder().message("username not match").status(false).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            ResultDto<?> response = ResultDto.builder().message("user not found").status(false).build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Token checkToken = tokenRepo.findByToken(req.getAccessToken() + "|" + req.getCode())
                .orElse(null);
        if (checkToken == null) {
            ResultDto<?> response = ResultDto.builder().message("token invalid").status(false).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepo.save(user);
        ResultDto<?> response = ResultDto.builder().message("reset password success!").status(true).build();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResultDto<?>> checkCodeReset(ResetPasswordReq req) {
        try {
            if (jwtService.extractExpiration(req.getAccessToken()).before(new Date())) {
                ResultDto<?> response = ResultDto.builder().message("token expired").status(false).build();
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (NullPointerException e) {
            ResultDto<?> response = ResultDto.builder().message("token expired").status(false).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().message("send mail fail").status(false).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String email = jwtService.extractUsername(req.getAccessToken());
        if (!email.equals(req.getEmail())) {
            ResultDto<?> response = ResultDto.builder().message("username not match").status(false).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            ResultDto<?> response = ResultDto.builder().message("user not found").status(false).build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Token checkToken = tokenRepo.findByToken(req.getAccessToken() + "|" + req.getCode().toString())
                .orElse(null);
        if (checkToken == null) {
            ResultDto<?> response = ResultDto.builder().message("code invalid").status(false).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        ResultDto<?> response = ResultDto.builder().message("valid code").status(true).build();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResultDto<?>> refreshToken(HttpServletRequest request, HttpServletResponse response,
            TypeDevice typeDevice) {
        String refreshToken;
        String userEmail;
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ResultDto<?> result = ResultDto.builder().message("header Authorization or Bearer not found ").status(false)
                    .build();
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepo.findByEmail(userEmail).orElseThrow();
            if (jwtService.inValidToken(refreshToken, user)) {
                String accessToken = jwtService.generateAccessToken(user);
                // revoke token old by type device
                revokeAllUserTokens(user, typeDevice);

                // save token new by type device
                var tokenUser = Token.builder()
                        .user(user)
                        .token(accessToken)
                        .expired(false)
                        .revoked(false)
                        .typeDevice(typeDevice)
                        .isResetPassword(false)
                        .build();
                tokenRepo.save(tokenUser);

                ResultDto<?> result = ResultDto.builder().message("refresh token success").model(accessToken)
                        .status(true).build();

                return ResponseEntity.ok(result);
            }
        }

        ResultDto<?> result = ResultDto.builder().message("token invalid")
                .status(false).build();
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    //

    private boolean isValidAccessTokenGG(String access_token_google) {
        String urlGGCheck = "https://www.googleapis.com/oauth2/v3/userinfo";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer " + access_token_google);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<String> response = restTemplate.exchange(urlGGCheck, HttpMethod.GET, entity, String.class);

        return response.getStatusCode().toString().contains("OK");
    }

    private void revokeAllUserTokens(User user, TypeDevice typeDevice) {
        var validUserTokens = tokenRepo.findAllValidTokensByUser(user.getId(), typeDevice);
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(o -> {
            o.setExpired(true);
            o.setRevoked(true);
        });
        tokenRepo.saveAll(validUserTokens);
    }

}
