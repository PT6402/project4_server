package fpt.aptech.project4_server.security.oauth2;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import fpt.aptech.project4_server.entities.auth.AuthProvider;
import fpt.aptech.project4_server.entities.auth.Role;
import fpt.aptech.project4_server.entities.auth.User;
import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.repository.UserDetailRepo;
import fpt.aptech.project4_server.repository.auth.UserRepo;
import fpt.aptech.project4_server.security.UserGlobal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2Service extends DefaultOAuth2UserService {
  private final UserRepo userRepo;
  private final UserDetailRepo userDetailRepo;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    var oauth2_user = super.loadUser(userRequest);
    try {
      return handleGetInforUserByOAuth2(userRequest, oauth2_user);
    } catch (AuthenticationException authEx) {
      throw authEx;
    } catch (Exception e) {
      log.error("erro prcess oauth get user {}", e.getMessage());
      throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
    }
  }

  private OAuth2User handleGetInforUserByOAuth2(OAuth2UserRequest userquest, OAuth2User oauth2_user) {

    OAuth2UserInfor oAuth2UserInfo = new OAuth2GoogleUserInfor(oauth2_user.getAttributes());
    if (!StringUtils.hasLength(oAuth2UserInfo.getEmail())) {
      new Exception("can not load infor user by OAuth2 GOOGLE");
    }

    var userOptional = userRepo.findByEmail(oAuth2UserInfo.getEmail());
    User user;
    if (userOptional.isEmpty()) {
      user = userRepo.save(User.builder()
          .email(oAuth2UserInfo.getEmail())
          .role(Role.user)
          .provider(AuthProvider.google)
          .build());
      userDetailRepo.save(UserDetail.builder().fullname(oAuth2UserInfo.getName()).user(user).build());
    } else {
      user = userOptional.get();
    }

    return UserGlobal.createUser(user, oauth2_user.getAttributes());
  }

}