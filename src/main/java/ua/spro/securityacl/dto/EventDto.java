package ua.spro.securityacl.dto;

import java.time.LocalDateTime;
import java.util.List;

public record EventDto(
    Long id,
    String title,
    String description,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime,
    List<UserDto> users) {}
