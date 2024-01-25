package com.emmsale.login.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OAuthProviderTypeTest {

  @Test
  @DisplayName("typeName으로 적절한 OAuthProviderType을 제공한다.")
  void from() {
    final String name = "github";

    final OAuthProviderType actual = OAuthProviderType.from(name);

    assertThat(actual)
        .isEqualTo(OAuthProviderType.GITHUB);
  }
}
