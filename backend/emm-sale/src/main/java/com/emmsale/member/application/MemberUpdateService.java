package com.emmsale.member.application;

import com.emmsale.image.application.S3Client;
import com.emmsale.member.application.dto.DescriptionRequest;
import com.emmsale.member.application.dto.MemberImageResponse;
import com.emmsale.member.application.dto.MemberProfileResponse;
import com.emmsale.member.application.dto.OpenProfileUrlRequest;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberUpdateService {

  private final MemberRepository memberRepository;
  private final S3Client s3Client;

  public void updateOpenProfileUrl(
      final Member member,
      final OpenProfileUrlRequest openProfileUrlRequest
  ) {
    final Member persistMember = memberRepository.findById(member.getId())
        .orElseThrow(() -> new MemberException((MemberExceptionType.NOT_FOUND_MEMBER)));
    final String openProfileUrl = openProfileUrlRequest.getOpenProfileUrl();

    persistMember.updateOpenProfileUrl(openProfileUrl);
  }

  public void updateDescription(final Member member, final DescriptionRequest descriptionRequest) {
    final String description = descriptionRequest.getDescription();

    final Member persistMember = memberRepository.findById(member.getId())
        .orElseThrow(() -> new MemberException((MemberExceptionType.NOT_FOUND_MEMBER)));
    persistMember.updateDescription(description);
  }

  public void deleteMember(final Member member, final Long memberId) {
    if (member.isNotMe(memberId)) {
      throw new MemberException(MemberExceptionType.FORBIDDEN_DELETE_MEMBER);
    }

    memberRepository.deleteById(memberId);
  }

  public MemberImageResponse updateMemberProfile(
      final MultipartFile image,
      final Long memberId,
      final Member member
  ) {
    if (member.isNotMe(memberId)) {
      throw new MemberException(MemberExceptionType.FORBIDDEN_UPDATE_PROFILE_IMAGE);
    }

    if (member.isNotGithubProfile()) {
      final String imageName = s3Client.convertImageName(member.getImageUrl());
      s3Client.deleteImages(List.of(imageName));
    }

    final String imageName = s3Client.uploadImage(image);
    final String imageUrl = s3Client.convertImageUrl(imageName);
    member.updateProfile(imageUrl);

    return new MemberImageResponse(imageUrl);
  }
}
