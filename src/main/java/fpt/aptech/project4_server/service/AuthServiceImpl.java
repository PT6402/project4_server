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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final UserRepo userRepo;
    private final TokenRepo tokenRepo;
    private final UserDetailRepo userDetailRepo;

    private final JwtService jwtService;
    private final MailService mailService;

    @Override
    public ResponseEntity<?> register(RegisterReq req) {
        User checkExist = userRepo.findByEmail(req.getEmail()).orElse(null);

        if (checkExist != null) {
            return new ResponseEntity<>("user exist", HttpStatus.CONFLICT);
        }

        // create user
        User newUser = User.builder()
                .email(req.getEmail())
                .role(TypeRole.USER)
                .build();

        if (req.getTypeLogin().equals(TypeLogin.GOOGLE)) {
            if (!isValidAccessTokenGG(req.getPassword())) {
                return new ResponseEntity<>("invalid token", HttpStatus.BAD_REQUEST);
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

            var response = AutheRes.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .email(userSaved.getEmail())
                    .fullname(newUserDetailSaved.getFullname())
                    .role(userSaved.getRole())
                    .typeLogin(userSaved.getTypeLogin())
                    .build();
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok("register successfully !");
    }

    @Override
    public ResponseEntity<?> authenticate(AuthReq req) {
        // check exist account
        User user = userRepo.findByEmail(req.getEmail()).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);
        }

        // check type login
        if (req.getTypeLogin().equals(TypeLogin.GOOGLE)) {
            if (!isValidAccessTokenGG(req.getPassword())) {
                return new ResponseEntity<>("invalid token", HttpStatus.BAD_REQUEST);
            }

            // TODO: check email match email in google
        } else {
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
            } catch (AuthenticationException e) {
                return new ResponseEntity<>("password wrond", HttpStatus.BAD_REQUEST);
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

        var response = AutheRes.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .typeLogin(user.getTypeLogin())
                .fullname(userDetailRepo.findByUser(user).orElse(null).getFullname())
                .role(user.getRole())
                .build();
        return ResponseEntity.ok(response);

    }

    @Override
    public ResponseEntity<?> checkTypeLogin(String email) {
        var user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(user.getTypeLogin());
        }
    }

    @Override
    public ResponseEntity<?> forgotPassword(String email) {

        try {
            var user = userRepo.findByEmail(email).orElse(null);

            if (user == null) {
                return new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);
            }

            if (user.getTypeLogin().equals(TypeLogin.GOOGLE)) {
                return new ResponseEntity<>("type invalid", HttpStatus.BAD_REQUEST);
            }

            Random random = new Random();
            int randomNumber = random.nextInt(10000);
            var checkSendMail = mailService.sendMail(email, "reset password",
                    "this is code to reset password: " + String.format("%04d", randomNumber));
            if (!checkSendMail) {
                return new ResponseEntity<>("mail send fail!", HttpStatus.BAD_REQUEST);
            } else {
                String accessToken = jwtService.generateAccessToken(user);

                var token = Token.builder()
                        .user(user)
                        .token(accessToken + "|" + String.format("%04d",
                                randomNumber))
                        .expired(false)
                        .revoked(false)
                        .isResetPassword(true)
                        .build();
                tokenRepo.save(token);

                return ResponseEntity.ok(accessToken);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("mail send fail!", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> resetPasswordUser(ResetPasswordReq req) {
        if (jwtService.extractExpiration(req.getAccessToken()).before(new Date())) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }
        String email = jwtService.extractUsername(req.getAccessToken());
        if (!email.equals(req.getEmail())) {
            return new ResponseEntity<>("username not match", HttpStatus.BAD_REQUEST);
        }

        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);
        }

        Token checkToken = tokenRepo.findByToken(req.getAccessToken() + "|" + req.getCode())
                .orElse(null);
        if (checkToken == null) {
            return new ResponseEntity<>("token invalid", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepo.save(user);
        return ResponseEntity.ok("reset password success!");
    }

    @Override
    public ResponseEntity<?> checkCodeReset(ResetPasswordReq req) {
        if (jwtService.extractExpiration(req.getAccessToken()).before(new Date())) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }
        String email = jwtService.extractUsername(req.getAccessToken());
        if (!email.equals(req.getEmail())) {
            return new ResponseEntity<>("username not match", HttpStatus.BAD_REQUEST);
        }

        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);
        }
        log.info("this is code: " + req.getCode());
        Token checkToken = tokenRepo.findByToken(req.getAccessToken() + "|" + req.getCode().toString())
                .orElse(null);
        if (checkToken == null) {
            return new ResponseEntity<>("token invalid", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("valid code");
    }

    @Override
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response,
            TypeDevice typeDevice) {
        String refreshToken;
        String userEmail;
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("header Authorization or Bearer not found ", HttpStatus.BAD_REQUEST);
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

                return ResponseEntity.ok(accessToken);
            }
        }

        return new ResponseEntity<>("token invalid", HttpStatus.BAD_REQUEST);
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
