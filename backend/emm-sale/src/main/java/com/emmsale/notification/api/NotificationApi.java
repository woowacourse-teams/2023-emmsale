package com.emmsale.notification.api;

import com.emmsale.member.domain.Member;
import com.emmsale.notification.application.NotificationQueryService;
import com.emmsale.notification.application.dto.NotificationAllResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationApi {

  private final NotificationQueryService notificationQueryService;

  @GetMapping("/notifications")
  public List<NotificationAllResponse> find(
      final Member authMember,
      @RequestParam("member-id") final Long loginMemberId
  ) {
    return notificationQueryService.findAllByMemberId(authMember, loginMemberId);
  }
}
