package fpt.aptech.project4_server.security.oauth2;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import fpt.aptech.project4_server.config.AppProperties;

import fpt.aptech.project4_server.service.auth_google.AuthGGService;
import fpt.aptech.project4_server.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static fpt.aptech.project4_server.security.oauth2.OAuth2Cookie.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandleOAuth2Success extends SimpleUrlAuthenticationSuccessHandler {
  private final AppProperties appProperties;
  private final OAuth2Cookie auth2Cookie;
  private final AuthGGService gg_service;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    String targetUrl = determineTargetUrl(request, response, authentication);

    if (response.isCommitted()) {
      log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
      return;
    }

    clearAuthenticationAttributes(request, response);
    gg_service.loginWeb(authentication, response, request);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
        .map(Cookie::getValue);

    if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
      new Exception(
          "Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
    }

    String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

    return UriComponentsBuilder.fromUriString(targetUrl)
        // .queryParam("status", "success")
        .build().toUriString();
  }

  protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    auth2Cookie.removeAuthorizationRequestCookies(request, response);
  }

  private boolean isAuthorizedRedirectUri(String uri) {
    URI clientRedirectUri = URI.create(uri);

    return appProperties.getOAuth2().getAuthorized_redirec_uris()
        .stream()
        .anyMatch(authorizedRedirectUri -> {
          URI authorizedURI = URI.create(authorizedRedirectUri);
          if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
              && authorizedURI.getPort() == clientRedirectUri.getPort()) {
            return true;
          }
          return false;
        });
  }

}
