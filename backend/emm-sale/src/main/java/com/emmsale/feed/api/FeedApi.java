package com.emmsale.feed.api;

import com.emmsale.feed.application.FeedCommandService;
import com.emmsale.feed.application.FeedQueryService;
import com.emmsale.feed.application.dto.FeedDetailResponse;
import com.emmsale.feed.application.dto.FeedListResponse;
import com.emmsale.feed.application.dto.FeedPostRequest;
import com.emmsale.feed.application.dto.FeedPostResponse;
import com.emmsale.feed.application.dto.FeedUpdateRequest;
import com.emmsale.feed.application.dto.FeedUpdateResponse;
import com.emmsale.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class FeedApi {

  private final FeedQueryService feedQueryService;
  private final FeedCommandService feedCommandService;

  @GetMapping
  public FeedListResponse findAllFeeds(@RequestParam("event-id") final Long eventId) {
    return feedQueryService.findAllFeeds(eventId);
  }

  @GetMapping("/{id}")
  public FeedDetailResponse findDetailFeed(@PathVariable final Long id) {
    return feedQueryService.findFeed(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FeedPostResponse postFeed(final Member member,
      @RequestBody final FeedPostRequest request) {
    return feedCommandService.postFeed(member, request);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public FeedUpdateResponse updateFeed(
      final Member member,
      @PathVariable final Long id,
      @RequestBody final FeedUpdateRequest request
  ) {
    return feedCommandService.updateFeed(member, id, request);
  }
}
