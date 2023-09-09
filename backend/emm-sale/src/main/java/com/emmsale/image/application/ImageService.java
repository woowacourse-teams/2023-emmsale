package com.emmsale.image.application;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.emmsale.image.exception.ImageException;
import com.emmsale.image.exception.ImageExceptionType;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {
  
  public static final String EXTENSION_DELIMITER = ".";
  private static List<String> ALLOWED_FILE_EXTENSIONS = List.of(".jpg", ".png", ".jpeg");
  
  @Value("${cloud.aws.s3.bucket}")
  private String bucket;
  
  private final AmazonS3 amazonS3;
  
  public List<String> uploadImages(final List<MultipartFile> multipartFiles) {
    List<String> fileNameList = new ArrayList<>();
    
    multipartFiles.forEach(file -> {
      final String fileExtension = extractFileExtension(file);
      
      String newFileName = UUID.randomUUID().toString().concat(fileExtension);
      ObjectMetadata objectMetadata = new ObjectMetadata();
      objectMetadata.setContentLength(file.getSize());
      objectMetadata.setContentType(file.getContentType());
      
      try (InputStream inputStream = file.getInputStream()) {
        amazonS3.putObject(new PutObjectRequest(bucket, newFileName, inputStream, objectMetadata));
      } catch (IOException e) {
        throw new ImageException(ImageExceptionType.FAIL_UPLOAD_IMAGE);
      }
      fileNameList.add(newFileName);
    });
    
    return fileNameList;
  }
  
  private String extractFileExtension(MultipartFile file) {
    final String originalFileName = file.getOriginalFilename();
    final int extensionIndex = Objects.requireNonNull(originalFileName)
        .lastIndexOf(EXTENSION_DELIMITER);
    if (extensionIndex == -1 || !ALLOWED_FILE_EXTENSIONS.contains(
        originalFileName.substring(extensionIndex))) {
      throw new ImageException(ImageExceptionType.INVALID_FILE_FORMAT);
    }
    return originalFileName.substring(extensionIndex);
  }
  
  public void deleteImages(final List<String> fileNames) {
    fileNames.forEach(fileName ->
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName)));
  }
}
