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

  @Value("${github.client.id}")
  private String clientId;
  @Value("${github.client.secret}")
  private String clientSecret;
  @Value("${github.url.access-token}")
  private String accessTokenUrl;
  @Value("${github.url.profile}")
  private String profileUrl;

  public String getAccessTokenFromGithub(final String code) {
    final GithubAccessTokenRequest githubAccessTokenRequest = new GithubAccessTokenRequest(
        code,
        clientId,
        clientSecret
    );

    final HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

    final HttpEntity httpEntity = new HttpEntity(githubAccessTokenRequest, headers);
    final RestTemplate restTemplate = new RestTemplate();

    final String accessToken = restTemplate
        .exchange(accessTokenUrl, HttpMethod.POST, httpEntity, GithubAccessTokenResponse.class)
        .getBody()
        .getAccessToken();

    if (accessToken == null) {
      throw new LoginException(LoginExceptionType.NOT_FOUND_GITHUB_ACCESS_TOKEN);
    }
    return accessToken;
  }

  public GithubProfileResponse getGithubProfileFromGithub(final String accessToken) {
    final HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "token " + accessToken);

    final HttpEntity httpEntity = new HttpEntity(headers);
    final RestTemplate restTemplate = new RestTemplate();

    try {
      return restTemplate
          .exchange(profileUrl, HttpMethod.GET, httpEntity, GithubProfileResponse.class)
          .getBody();
    } catch (final HttpClientErrorException e) {
      throw new LoginException(LoginExceptionType.INVALID_GITHUB_ACCESS_TOKEN);
    }
  }
}
