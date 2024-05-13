package ua.spro.securityacl.controller;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.spro.securityacl.dto.CreateUpdateEventDto;
import ua.spro.securityacl.dto.EventDto;
import ua.spro.securityacl.dto.UserDto;
import ua.spro.securityacl.entity.Event;
import ua.spro.securityacl.entity.User;
import ua.spro.securityacl.repository.EventRepository;
import ua.spro.securityacl.repository.UserRepository;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

  private final EventRepository eventRepository;
  private final UserRepository userRepository;

  @GetMapping("/created-by-me")
  public List<EventDto> getMyEvents(@AuthenticationPrincipal UserDetails userDetails) {
    String email = userDetails.getUsername();
    return userRepository.findByEmail(email).stream()
        .flatMap(user -> user.getCreatedEvents().stream())
        .map(this::mapToEventDto)
        .toList();
  }

  @GetMapping("/participating")
  public List<EventDto> getParticipatingEvents(@AuthenticationPrincipal UserDetails userDetails) {
    String email = userDetails.getUsername();
    return userRepository.findByEmail(email).stream()
        .flatMap(user -> user.getEvents().stream())
        .map(this::mapToEventDto)
        .toList();
  }

  @PostMapping
  @Transactional
  public EventDto createEvent(
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestBody CreateUpdateEventDto createEventDto) {
    User creator = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    Event event = new Event();
    event.setTitle(createEventDto.title());
    event.setDescription(createEventDto.description());
    event.setStartDateTime(createEventDto.startDateTime());
    event.setEndDateTime(createEventDto.endDateTime());
    event.setCreator(creator);
    event.setUsers(getParticipants(createEventDto));

    Event saved = eventRepository.save(event);

    return mapToEventDto(saved);
  }

  @PreAuthorize("@eventPermissionsService.hasPermissions(authentication, #id, 'WRITE')")
  @PatchMapping("/{id}")
  @Transactional
  public EventDto updateEvent(
      @PathVariable Long id, @RequestBody CreateUpdateEventDto updateEventDto) {
    Event event =
        eventRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

    event.setTitle(updateEventDto.title());
    event.setDescription(updateEventDto.description());
    event.setStartDateTime(updateEventDto.startDateTime());
    event.setEndDateTime(updateEventDto.endDateTime());
    event.setUsers(getParticipants(updateEventDto));

    Event saved = eventRepository.save(event);

    return mapToEventDto(saved);
  }

  private EventDto mapToEventDto(Event event) {
    return new EventDto(
        event.getId(),
        event.getTitle(),
        event.getDescription(),
        event.getStartDateTime(),
        event.getEndDateTime(),
        mapToUserDtos(event.getUsers()));
  }

  private List<UserDto> mapToUserDtos(List<User> users) {
    return users.stream()
        .map(user -> new UserDto(user.getEmail(), user.getFirstname(), user.getLastname()))
        .toList();
  }

  private List<User> getParticipants(CreateUpdateEventDto createUpdateEventDto) {
    if (createUpdateEventDto.userIds() != null) {
      return createUpdateEventDto.userIds().stream()
          .map(userRepository::findById) // Map each ID to an Optional<User>
          .map(Optional::orElseThrow)
          .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }
}
