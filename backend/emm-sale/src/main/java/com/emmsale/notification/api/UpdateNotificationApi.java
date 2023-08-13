package com.emmsale.notification.api;

import com.emmsale.member.domain.Member;
import com.emmsale.notification.application.UpdateNotificationQueryService;
import com.emmsale.notification.application.dto.UpdateNotificationResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UpdateNotificationApi {

  private final UpdateNotificationQueryService updateNotificationQueryService;

  @GetMapping("/update-notifications")
  public List<UpdateNotificationResponse> find(
      final Member authMember,
      @RequestParam("member-id") final Long loginMemberId
  ) {
    return updateNotificationQueryService.findAll(authMember, loginMemberId);
  }
}
