package com.emmsale.event.application.dto;

import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventType;
import com.emmsale.tag.application.dto.TagRequest;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@RequiredArgsConstructor
@Getter
public class EventDetailRequest {

  private static final String DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss";

  @NotBlank(message = "행사의 이름을 입력해 주세요.")
  private final String name;
  @NotBlank(message = "행사의 장소를 입력해 주세요.")
  private final String location;
  @NotBlank(message = "행사의 상세 URL을 입력해 주세요.")
  @Pattern(regexp = "(http.?://).*", message = "http:// 혹은 https://로 시작하는 주소를 입력해 주세요.")
  private final String informationUrl;

  @DateTimeFormat(pattern = DATE_TIME_FORMAT)
  @NotNull(message = "행사의 시작 일시를 입력해 주세요.")
  private final LocalDateTime startDateTime;
  @DateTimeFormat(pattern = DATE_TIME_FORMAT)
  @NotNull(message = "행사의 종료 일시를 입력해 주세요.")
  private final LocalDateTime endDateTime;

  @DateTimeFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime applyStartDateTime;
  @DateTimeFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime applyEndDateTime;

  private final List<TagRequest> tags;

  private final String imageUrl;
  private final EventType type;

  public Event toEvent() {
    return new Event(
        name,
        location,
        startDateTime,
        endDateTime,
        applyStartDateTime,
        applyEndDateTime,
        informationUrl,
        type,
        imageUrl
    );
  }
}
