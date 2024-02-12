package com.emmsale.member.domain;

import com.emmsale.activity.domain.Activity;
import com.emmsale.activity.domain.ActivityType;
import com.emmsale.base.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberActivity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Activity activity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Member member;

  public MemberActivity(final Activity activity, final Member member) {
    this.activity = activity;
    this.member = member;
  }

  public String getActivityName() {
    return activity.getName();
  }

  public Boolean isJobActivity() {
    return activity.getActivityType() == ActivityType.JOB;
  }
}
