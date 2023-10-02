package com.emmsale.image.application;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.emmsale.image.exception.ImageException;
import com.emmsale.image.exception.ImageExceptionType;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
//얘 @Component로 바꾸는 거 어떤가요? @Service보단 @Component가 더 적절한 것 같아요.
@RequiredArgsConstructor
public class S3Client {

  private static final String EXTENSION_DELIMITER = ".";
  private static final List<String> ALLOWED_FILE_EXTENSIONS = List.of(".jpg", ".png", ".jpeg");
  private static final int MIN_EXTENSION_SEPARATOR_INDEX = 0;
  private static final String URL_DELIMITER = "/";

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final AmazonS3 amazonS3;

  public List<String> uploadImages(final List<MultipartFile> multipartFiles) {
    return multipartFiles.stream().map(this::uploadImage)
        .collect(Collectors.toList());
  }

  public String uploadImage(final MultipartFile file) {
    final String fileExtension = extractFileExtension(file);
    final String newFileName = UUID.randomUUID().toString().concat(fileExtension);
    final ObjectMetadata objectMetadata = configureObjectMetadata(file);

    try (final InputStream inputStream = file.getInputStream()) {
      amazonS3.putObject(new PutObjectRequest(bucket, newFileName, inputStream, objectMetadata));
    } catch (final IOException | SdkClientException exception) {
      throw new ImageException(ImageExceptionType.FAIL_S3_UPLOAD_IMAGE);
    }
    return newFileName;
  }

  private String extractFileExtension(final MultipartFile file) {
    final String originalFileName = file.getOriginalFilename();
    final int extensionIndex = Objects.requireNonNull(originalFileName)
        .lastIndexOf(EXTENSION_DELIMITER);
    validateExtension(extensionIndex, originalFileName);
    return originalFileName.substring(extensionIndex);
  }

  private void validateExtension(final int extensionIndex, final String originalFileName) {
    if (extensionIndex >= MIN_EXTENSION_SEPARATOR_INDEX && ALLOWED_FILE_EXTENSIONS.contains(
        originalFileName.substring(extensionIndex))) {
      return;
    }
    throw new ImageException(ImageExceptionType.INVALID_FILE_FORMAT);
  }

  private ObjectMetadata configureObjectMetadata(final MultipartFile file) {
    final ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(file.getSize());
    objectMetadata.setContentType(file.getContentType());
    return objectMetadata;
  }

  public void deleteImages(final List<String> fileNames) {
    try {
      fileNames.forEach(fileName ->
          amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName)));
    } catch (SdkClientException exception) {
      throw new ImageException(ImageExceptionType.FAIL_S3_DELETE_IMAGE);
    }
  }

  public String convertImageUrl(final String imageName) {
    return String.join(URL_DELIMITER, bucket, imageName);
  }
}
