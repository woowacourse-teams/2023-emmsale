package com.emmsale.career.domain;

public enum Activity {

  CLUB("동아리"),
  CONFERENCE("컨퍼런스"),
  EDUCATION("교육"),
  JOB("분야");

  private final String value;

  Activity(final String value) {
    this.value = value;
  }
}
