package com.emmsale.image.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.emmsale.image.exception.ImageException;
import com.emmsale.image.exception.ImageExceptionType;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.BDDMockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class S3ClientTest {

  private static final String TEST_BUCKET = "Test";

  private S3Client s3Client;
  private AmazonS3 mockingAmazonS3;

  @BeforeEach
  void setUp() {
    mockingAmazonS3 = mock(AmazonS3.class);
    s3Client = new S3Client(TEST_BUCKET, mockingAmazonS3);
  }

  @Test
  @DisplayName("uploadImages(): 올바른 확장자를 갖는 파일을 입력받으면 정상적으로 파일을 S3에 업로드한다.")
  void uploadImages_success() {
    //given
    final List<MultipartFile> files = List.of(
        new MockMultipartFile("test", "test.png", "", new byte[]{}),
        new MockMultipartFile("test", "test.jpg", "", new byte[]{}),
        new MockMultipartFile("test", "test.jpeg", "", new byte[]{}));
    BDDMockito.given(mockingAmazonS3.putObject(any(PutObjectRequest.class)))
        .willReturn(new PutObjectResult());

    //when
    s3Client.uploadImages(files);

    //then
    verify(mockingAmazonS3, times(3))
        .putObject(any(PutObjectRequest.class));

  }

  @ParameterizedTest
  @ValueSource(strings = {"test.txt", "test"})
  @DisplayName("uploadImages(): 지원하지 않는 확장자를 갖는 파일을 입력받으면 예외를 반환한다.")
  void uploadImages_fail_extension(final String fileName) {
    //given
    final List<MultipartFile> files = List.of(
        new MockMultipartFile("test", fileName, "", new byte[]{}));

    //when
    final ThrowingCallable actual = () -> s3Client.uploadImages(files);

    //then
    Assertions.assertThatThrownBy(actual)
        .isInstanceOf(ImageException.class)
        .hasMessage(ImageExceptionType.INVALID_FILE_FORMAT.errorMessage());
  }

  @Test
  @DisplayName("deleteImages(): 올바른 확장자를 갖는 파일을 입력받으면 정상적으로 파일을 S3에 업로드한다.")
  void deleteImages_success() {
    //given
    final List<String> fileNames = List.of("test1", "test2", "test3");

    //when
    s3Client.deleteImages(fileNames);

    //then
    verify(mockingAmazonS3, times(3))
        .deleteObject(any(DeleteObjectRequest.class));
  }

  @Test
  @DisplayName("convertImageUrl(): 이미지 이름을 imageUrl로 바꾼다.")
  void convertImageUrl() {
    final String imageName = "image.png";
    final String expected = "Test/image.png";

    final String actual = s3Client.convertImageUrl(imageName);

    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  @DisplayName("convertImageName(): 이미지 이름을 imageName로 바꾼다.")
  void convertImageName() {
    final String imageUrl = "Test/image.png";
    final String expected = "image.png";

    final String actual = s3Client.convertImageName(imageUrl);

    assertThat(actual)
        .isEqualTo(expected);
  }
}
