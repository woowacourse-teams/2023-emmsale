package com.emmsale.resolver;

import com.emmsale.login.exception.LoginException;
import com.emmsale.login.exception.LoginExceptionType;
import com.emmsale.login.utils.AuthorizationExtractor;
import com.emmsale.login.utils.JwtTokenProvider;
import com.emmsale.login.utils.MemberOnly;
import com.emmsale.member.application.MemberQueryService;
import com.emmsale.member.domain.Member;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

  private final JwtTokenProvider jwtTokenProvider;
  private final MemberQueryService memberQueryService;

  @Override
  public boolean supportsParameter(final MethodParameter parameter) {
    return parameter.getParameterType().equals(Member.class) &&
        parameter.hasParameterAnnotation(MemberOnly.class);
  }

  @Override
  public Object resolveArgument(
      final MethodParameter parameter,
      final ModelAndViewContainer mavContainer,
      final NativeWebRequest webRequest,
      final WebDataBinderFactory binderFactory
  ) {
    final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
    final String token = AuthorizationExtractor.extract(request);

    if (token == null || token.isEmpty()) {
      throw new LoginException(LoginExceptionType.NOT_FOUND_AUTHORIZATION_TOKEN);
    }

    final Long memberId = Long.parseLong(jwtTokenProvider.extractSubject(token));

    return memberQueryService.findById(memberId);
  }
}
