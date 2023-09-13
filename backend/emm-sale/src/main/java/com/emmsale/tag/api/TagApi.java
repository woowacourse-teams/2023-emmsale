package com.emmsale.tag.api;

import com.emmsale.tag.application.TagCommandService;
import com.emmsale.tag.application.TagQueryService;
import com.emmsale.tag.application.dto.TagRequest;
import com.emmsale.tag.application.dto.TagResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagApi {

  private final TagQueryService queryService;
  private final TagCommandService commandService;

  @GetMapping
  public ResponseEntity<List<TagResponse>> findAll() {
    return ResponseEntity.ok(queryService.findAll());
  }

  @PostMapping
  public ResponseEntity<TagResponse> create(@RequestBody final TagRequest tagRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(commandService.addTag(tagRequest));
  }
}
