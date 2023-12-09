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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class S3Client {

  private static final String EXTENSION_DELIMITER = ".";
  private static final List<String> ALLOWED_FILE_EXTENSIONS = List.of(".jpg", ".png", ".jpeg");
  private static final int MIN_EXTENSION_SEPARATOR_INDEX = 0;
  private static final String URL_DELIMITER = "/";

  private final String bucket;
  private final AmazonS3 amazonS3;
  private final String cloudFrontPrefix;

  public S3Client(
      @Value("${cloud.aws.s3.bucket}") final String bucket,
      @Value("${cloud.aws.cloudfront-prefix}") final String cloudFrontPrefix,
      final AmazonS3 amazonS3
  ) {
    this.bucket = bucket;
    this.amazonS3 = amazonS3;
    this.cloudFrontPrefix = cloudFrontPrefix;
  }

  public List<String> uploadImages(final List<MultipartFile> multipartFiles) {
    return multipartFiles.stream()
        .parallel()
        .map(this::uploadImage)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  public String uploadImage(final MultipartFile file) {
    final String fileExtension = extractFileExtension(file);
    final String newFileName = UUID.randomUUID().toString().concat(fileExtension);
    final ObjectMetadata objectMetadata = configureObjectMetadata(file);

    try (final InputStream inputStream = file.getInputStream()) {
      amazonS3.putObject(new PutObjectRequest(bucket, newFileName, inputStream, objectMetadata));
      return newFileName;
    } catch (final IOException | SdkClientException exception) {
      throw new ImageException(ImageExceptionType.FAIL_S3_UPLOAD_IMAGE);
    }
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
      fileNames.stream()
          .parallel()
          .forEach(fileName ->
              amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName)));
    } catch (SdkClientException exception) {
      throw new ImageException(ImageExceptionType.FAIL_S3_DELETE_IMAGE);
    }
  }

  public String convertImageUrl(final String imageName) {
    return String.join(URL_DELIMITER, cloudFrontPrefix, imageName);
  }

  public String convertImageName(final String imageUrl) {
    return imageUrl.split(cloudFrontPrefix + URL_DELIMITER, 2)[1];
  }
}
