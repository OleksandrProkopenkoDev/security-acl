package ua.spro.securityacl.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;


public record EventDto(
    @Getter Long id,
    String title,
    String description,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime,
    List<UserDto> users) {}
