package ru.practicum.main.server.events.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main.server.events.dto.EventFullDto;
import ru.practicum.main.server.events.dto.EventShortDto;
import ru.practicum.main.server.events.model.Event;
import ru.practicum.main.server.events.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByCategory_IdInAndPaidAndPublishedOnAfterAndPublishedOnBeforeAndAnnotationContainingIgnoreCase(List<Long> categoryIds,
                                                                                     Boolean paid, LocalDateTime start, LocalDateTime end, String text, Pageable pageable);

    List<EventFullDto> findAllByInitiator_IdInAndCategory_IdInAndStateInAndPublishedOnAfterAndPublishedOnBefore(List<Long> userIds, List<Long> categories,
                                                                                                                List<State> stateList, LocalDateTime start,
                                                                                                                LocalDateTime end, PageRequest pageRequest);

    List<Event> findAllByInitiator_Id(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiator_Id(Long id, Long userId);

    Optional<Event> findByIdAndState(Long eventId, State state);


}
