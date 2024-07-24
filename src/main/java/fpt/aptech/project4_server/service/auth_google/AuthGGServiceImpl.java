package fpt.aptech.project4_server.service.auth_google;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fpt.aptech.project4_server.config.AppProperties;
import fpt.aptech.project4_server.entities.auth.AuthProvider;
import fpt.aptech.project4_server.entities.auth.Role;
import fpt.aptech.project4_server.entities.auth.Token;
import fpt.aptech.project4_server.entities.auth.User;
import fpt.aptech.project4_server.repository.auth.TokenRepo;
import fpt.aptech.project4_server.repository.auth.UserRepo;
import fpt.aptech.project4_server.security.CusUserDetailsService;
import fpt.aptech.project4_server.service.jwt.JwtService;
import fpt.aptech.project4_server.util.CookieUtils;
import fpt.aptech.project4_server.util.ResultDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthGGServiceImpl implements AuthGGService {
  private final UserRepo userRepo;
  private final TokenRepo tokeRepo;
  private final JwtService jwtService;
  private final AppProperties appProperties;
  private final CusUserDetailsService customUserDetailsService;

  @Override
  public void loginWeb(Authentication auth, HttpServletResponse httpResponse, HttpServletRequest httpRequest) {
    String token = jwtService.generateRefreshToken(auth);
    tokeRepo.save(Token.builder()
        .ipAddress(httpRequest.getRemoteAddr())
        .inforDevice(httpRequest.getHeader("User-Agent"))
        .refreshToken(token).user(userRepo.findByEmail(auth.getName()).get()).build());
    CookieUtils.addCookie(httpResponse, "refreshToken", token,
        Integer.parseInt(appProperties.getCors().getMax_age_secs()));
  }

  @Override
  public ResponseEntity<ResultDto<?>> loginMobile(String token, HttpServletResponse httpResponse,
      HttpServletRequest httpRequest) {
    ResultDto<Map<String, Object>> result = checkTokenGG(token);
    String email = result.getModel().get("email").toString();
    User user = userRepo.findByEmail(email).orElse(null);
    if (user == null) {
      user = userRepo.save(User.builder()
          .email(email)
          .password(null)
          .provider(AuthProvider.google)
          .role(Role.user)
          .build());
    }
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
    Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null,
        userDetails.getAuthorities());

    var refreshToken = jwtService.generateRefreshToken(auth);
    var accessToken = jwtService.generateAccessToken(auth);
    tokeRepo.save(Token.builder()
        .refreshToken(refreshToken)
        .inforDevice(httpRequest.getHeader("User-Agent"))
        .ipAddress(httpRequest.getRemoteAddr())
        .user(user).build());
    CookieUtils.addCookie(httpResponse, "refreshToken",
        refreshToken,
        Integer.parseInt(appProperties.getCors().getMax_age_secs()));
    ResultDto<?> response = ResultDto.builder().status(true).message("login google success").model(accessToken).build();
    return ResponseEntity.ok(response);

  }

  @SuppressWarnings("unchecked")
  private ResultDto<Map<String, Object>> checkTokenGG(String token) {
    String urlGGCheck = "https://www.googleapis.com/oauth2/v3/userinfo";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + token);
    HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
    ResultDto<Map<String, Object>> result = new ResultDto<Map<String, Object>>();
    result.setModel(null);
    result.setStatus(false);
    try {
      var response = restTemplate.exchange(urlGGCheck, HttpMethod.GET, entity, String.class);
      if (response.getStatusCode().is2xxSuccessful()) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(response.getBody(), Map.class);
        result.setModel(map);
        result.setStatus(true);
      }
    } catch (RestClientException e) {
      result.setMessage("error restClientException");
    } catch (JsonMappingException e) {
      result.setMessage("error jsonMappingException");
    } catch (JsonProcessingException e) {
      result.setMessage("error jsonProcessingException");
    }
    return result;
  }
}
