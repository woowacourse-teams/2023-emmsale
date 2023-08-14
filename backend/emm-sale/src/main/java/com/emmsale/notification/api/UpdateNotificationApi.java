package com.emmsale.notification.api;

import com.emmsale.member.domain.Member;
import com.emmsale.notification.application.UpdateNotificationCommandService;
import com.emmsale.notification.application.UpdateNotificationQueryService;
import com.emmsale.notification.application.dto.UpdateNotificationResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UpdateNotificationApi {

  private final UpdateNotificationQueryService updateNotificationQueryService;
  private final UpdateNotificationCommandService updateNotificationCommandService;

  @GetMapping("/update-notifications")
  public List<UpdateNotificationResponse> find(
      final Member authMember,
      @RequestParam("member-id") final Long loginMemberId
  ) {
    return updateNotificationQueryService.findAll(authMember, loginMemberId);
  }

  @PutMapping("/update-notifications/{update-notifications-id}/read")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void read(
      final Member authMember,
      @PathVariable("update-notifications-id") final Long notificationId
  ) {
    updateNotificationCommandService.read(authMember, notificationId);
  }

  @DeleteMapping("/update-notifications")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBatch(
      final Member authMember,
      @RequestParam("delete-ids") final List<Long> deleteIds
  ) {
    updateNotificationCommandService.deleteBatch(authMember, deleteIds);
  }
}
