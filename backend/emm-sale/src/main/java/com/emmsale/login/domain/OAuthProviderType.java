package com.emmsale.login.domain;

import java.util.Arrays;

public enum OAuthProviderType {

  APPLE("apple"),
  GITHUB("github");

  private final String typeName;

  OAuthProviderType(final String typeName) {
    this.typeName = typeName;
  }

  public static OAuthProviderType from(final String typeName) {
    return Arrays.stream(values())
        .filter(type -> type.typeName.equals(typeName))
        .findAny()
        .orElseThrow();
  }

  public String getTypeName() {
    return typeName;
  }
}
