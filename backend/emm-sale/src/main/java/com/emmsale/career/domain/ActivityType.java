package com.emmsale.career.domain;

import lombok.Getter;

@Getter
public enum ActivityType {

  CLUB("동아리"),
  CONFERENCE("컨퍼런스"),
  EDUCATION("교육"),
  JOB("직무");

  private final String value;

  ActivityType(final String value) {
    this.value = value;
  }
}
