package com.emmsale.config;

import com.emmsale.login.utils.JwtTokenProvider;
import com.emmsale.member.application.MemberQueryService;
import com.emmsale.resolver.MemberArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class ArgumentResolverConfig implements WebMvcConfigurer {

  private final JwtTokenProvider jwtTokenProvider;
  private final MemberQueryService memberQueryService;

  @Override
  public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new MemberArgumentResolver(jwtTokenProvider, memberQueryService));
  }
}
