package com.emmsale.image.api;

import com.emmsale.image.application.ImageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageTestController {
  /*
   * dev 서버에서 ImageService의 기능들이 무사히 동작하는지 여부를 테스트를 하기 위해 임시로 만든 Controller입니다.
   * 동작 확인 후 새 이슈에서 삭제할 예정입니다.
   * */
  
  private final ImageService imageService;
  
  @PostMapping("/files")
  public ResponseEntity<List<String>> uploadImages(
      @RequestPart List<MultipartFile> multipartFile) {
    return ResponseEntity.ok(imageService.uploadImages(multipartFile));
  }
  
  @DeleteMapping("/files")
  public ResponseEntity<Void> deleteImages(@RequestParam List<String> fileNames) {
    imageService.deleteImages(fileNames);
    return null;
  }
}
