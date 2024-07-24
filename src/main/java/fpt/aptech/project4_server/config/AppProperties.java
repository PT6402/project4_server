package fpt.aptech.project4_server.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@ConfigurationProperties(prefix = "app")
public class AppProperties {
  private final Auth auth = new Auth();
  private final OAuth2 oAuth2 = new OAuth2();
  private final Cors cors = new Cors();

  @Setter
  @Getter
  public static class Auth {
    private String token_secret;
    private long refresh_token_expired;
    private long access_token_expired;
    private long reset_pass_token_expired;
  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class OAuth2 {
    private List<String> authorized_redirec_uris = new ArrayList<>();
  }

  @Getter
  @Setter
  public static class Cors {
    private List<String> allowed_origins = new ArrayList<>();
    private String max_age_secs;
  }
}
