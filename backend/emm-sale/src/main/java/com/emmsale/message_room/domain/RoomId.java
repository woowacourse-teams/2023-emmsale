package com.emmsale.message_room.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomId implements Serializable {

  private String uuid;

  private Long userId;

  public RoomId(final String uuid, final Long userId) {
    this.uuid = uuid;
    this.userId = userId;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final RoomId roomId = (RoomId) o;
    return Objects.equals(uuid, roomId.uuid)
        && Objects.equals(userId, roomId.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, userId);
  }
}
