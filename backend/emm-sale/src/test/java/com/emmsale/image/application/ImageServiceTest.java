package com.emmsale.image.application;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.image.exception.ImageException;
import com.emmsale.image.exception.ImageExceptionType;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class ImageServiceTest extends ServiceIntegrationTestHelper {
  
  @Autowired
  private ImageService imageService;
  
  @Test
  @DisplayName("uploadImages(): 지원하지 않는 확장자를 갖는 파일을 입력받으면 예외를 반환한다.")
  void uploadImages_fail_extension() {
    //given
    final List<MultipartFile> files = List.of(
        new MockMultipartFile("test", "test.txt", "", new byte[]{}));
    
    //when
    final ThrowingCallable actual = () -> imageService.uploadImages(files);
    
    //then
    Assertions.assertThatThrownBy(actual)
        .isInstanceOf(ImageException.class)
        .hasMessage(ImageExceptionType.INVALID_FILE_FORMAT.errorMessage());
  }
}