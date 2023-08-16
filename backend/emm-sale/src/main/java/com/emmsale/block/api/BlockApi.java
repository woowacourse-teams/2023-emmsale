package com.emmsale.block.api;

import com.emmsale.block.application.BlockCommandService;
import com.emmsale.block.application.BlockQueryService;
import com.emmsale.block.application.dto.BlockRequest;
import com.emmsale.block.application.dto.BlockResponse;
import com.emmsale.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blocks")
@RequiredArgsConstructor
public class BlockApi {

  public final BlockCommandService blockCommandService;
  public final BlockQueryService blockQueryService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void register(@RequestBody final BlockRequest blockRequest, final Member member) {
    blockCommandService.register(member, blockRequest);
  }

  @DeleteMapping("/{block-id}")
  public ResponseEntity<Void> unregister(@PathVariable("block-id") final Long blockId,
      final Member member) {
    blockCommandService.unregister(blockId, member);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<BlockResponse>> findBlockMembers(final Member member) {
    final List<BlockResponse> responses = blockQueryService.findAll(member);
    return ResponseEntity.ok(responses);
  }
}
