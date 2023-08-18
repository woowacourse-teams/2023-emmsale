package com.emmsale.tag.api;

import com.emmsale.tag.application.TagQueryService;
import com.emmsale.tag.application.dto.TagResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagApi {

  private final TagQueryService queryService;

  @GetMapping
  public ResponseEntity<List<TagResponse>> findAll() {
    return ResponseEntity.ok(queryService.findAll());
  }
}
