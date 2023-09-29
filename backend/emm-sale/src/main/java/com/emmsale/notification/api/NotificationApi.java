package com.emmsale.notification.api;

import com.emmsale.member.domain.Member;
import com.emmsale.notification.application.NotificationCommandService;
import com.emmsale.notification.application.NotificationQueryService;
import com.emmsale.notification.application.dto.NotificationAllResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationApi {

  private final NotificationQueryService notificationQueryService;
  private final NotificationCommandService notificationCommandService;

  @GetMapping("/notifications")
  public List<NotificationAllResponse> find(
      final Member authMember,
      @RequestParam("member-id") final Long loginMemberId
  ) {
    return notificationQueryService.findAllByMemberId(authMember, loginMemberId);
  }

  @PatchMapping("/notifications/{notification-id}/read")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void read(
      final Member member,
      @PathVariable("notification-id") final Long notificationId
  ) {
    notificationCommandService.read(member, notificationId);
  }
}
