package com.emmsale.scrap.application;

import com.emmsale.member.domain.Member;
import com.emmsale.scrap.application.dto.ScrapResponse;
import com.emmsale.scrap.domain.Scrap;
import com.emmsale.scrap.domain.ScrapRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapQueryService {

  private final ScrapRepository scrapRepository;

  public List<ScrapResponse> findAllScraps(final Member member) {
    final List<Scrap> scraps = scrapRepository.findAllByMemberId(member.getId());

    return scraps.stream()
        .map(ScrapResponse::from)
        .collect(Collectors.toList());
  }

}
