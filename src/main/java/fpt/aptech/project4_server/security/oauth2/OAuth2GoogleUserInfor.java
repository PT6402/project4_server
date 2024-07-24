package fpt.aptech.project4_server.security.oauth2;

import java.util.Map;

public class OAuth2GoogleUserInfor extends OAuth2UserInfor {

  public OAuth2GoogleUserInfor(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getId() {
    return (String) attributes.get("sub");
  }

  @Override
  public String getEmail() {
    return (String) attributes.get("email");
  }

  @Override
  public String getName() {
    return (String) attributes.get("name");
  }

}
