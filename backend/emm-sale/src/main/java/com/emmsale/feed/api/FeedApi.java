package com.emmsale.feed.api;

import com.emmsale.feed.application.FeedCommandService;
import com.emmsale.feed.application.dto.FeedPostRequest;
import com.emmsale.feed.application.dto.FeedPostResponse;
import com.emmsale.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class FeedApi {

  private final FeedCommandService feedCommandService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FeedPostResponse postFeed(final Member member,
      @RequestBody final FeedPostRequest request) {
    return feedCommandService.postFeed(member, request);
  }
}
