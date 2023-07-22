package com.emmsale.login.application;

import com.emmsale.login.application.dto.GithubAccessTokenRequest;
import com.emmsale.login.application.dto.GithubAccessTokenResponse;
import com.emmsale.login.application.dto.GithubProfileResponse;
import com.emmsale.login.exception.LoginException;
import com.emmsale.login.exception.LoginExceptionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class GithubClient {

  private static final RestTemplate REST_TEMPLATE = new RestTemplate();

  @Value("${github.client.id}")
  private String clientId;
  @Value("${github.client.secret}")
  private String clientSecret;
  @Value("${github.url.access-token}")
  private String accessTokenUrl;
  @Value("${github.url.profile}")
  private String profileUrl;

  public String getAccessTokenFromGithub(final String code) {
    final GithubAccessTokenRequest githubAccessTokenRequest = buildGithubAccessTokenRequest(code);

    try {
      final String accessToken = getAccessTokenResponse(githubAccessTokenRequest);
      throwIfNotFoundAccessToken(accessToken);
      return accessToken;
    } catch (final HttpClientErrorException httpClientErrorException) {
      throw new LoginException(LoginExceptionType.INVALID_GITHUB_CODE);
    }
  }

  public GithubProfileResponse getGithubProfileFromGithub(final String accessToken) {
    try {
      return getGithubProfileResponse(accessToken);
    } catch (final HttpClientErrorException httpClientErrorException) {
      throw new LoginException(LoginExceptionType.INVALID_GITHUB_ACCESS_TOKEN);
    }
  }

  private GithubProfileResponse getGithubProfileResponse(final String accessToken) {
    final HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, "token " + accessToken);

    final HttpEntity<String> httpEntity = new HttpEntity<>(headers);

    return REST_TEMPLATE
        .exchange(profileUrl, HttpMethod.GET, httpEntity, GithubProfileResponse.class)
        .getBody();
  }

  private GithubAccessTokenRequest buildGithubAccessTokenRequest(final String code) {
    return new GithubAccessTokenRequest(
        code,
        clientId,
        clientSecret
    );
  }

  private String getAccessTokenResponse(final GithubAccessTokenRequest githubAccessTokenRequest) {
    final HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

    final HttpEntity<GithubAccessTokenRequest> httpEntity = new HttpEntity<>(
        githubAccessTokenRequest,
        headers
    );

    return REST_TEMPLATE
        .exchange(accessTokenUrl, HttpMethod.POST, httpEntity, GithubAccessTokenResponse.class)
        .getBody()
        .getAccessToken();
  }

  private void throwIfNotFoundAccessToken(final String accessToken) {
    if (accessToken == null || accessToken.isBlank()) {
      throw new LoginException(LoginExceptionType.NOT_FOUND_GITHUB_ACCESS_TOKEN);
    }
  }
}
