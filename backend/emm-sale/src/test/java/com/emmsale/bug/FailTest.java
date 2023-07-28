package com.emmsale.bug;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FailTest {

  @Test
  @DisplayName("빌드 테스트를 위한 실패 테스트")
  void Test() {
    throw new IllegalStateException();
  }
}
