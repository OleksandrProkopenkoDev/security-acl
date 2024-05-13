package ua.spro.securityacl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.spro.securityacl.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {}
