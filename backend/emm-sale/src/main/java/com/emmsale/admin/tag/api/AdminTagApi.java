package com.emmsale.admin.tag.api;

import com.emmsale.admin.tag.application.TagCommandService;
import com.emmsale.tag.application.dto.TagRequest;
import com.emmsale.tag.application.dto.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/tags")
@RequiredArgsConstructor
public class AdminTagApi {

  private final TagCommandService commandService;

  @PostMapping
  public ResponseEntity<TagResponse> create(@RequestBody final TagRequest tagRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(commandService.addTag(tagRequest));
  }
}
