package ua.spro.securityacl.dto;

import java.time.LocalDateTime;
import java.util.List;
import ua.spro.securityacl.entity.User;

public record CreateUpdateEventDto(
    String title,
    String description,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime,
    User creator,
    List<Long> userIds) {}
