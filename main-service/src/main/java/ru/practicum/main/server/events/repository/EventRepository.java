package ru.practicum.main.server.events.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main.server.events.model.Event;
import ru.practicum.main.server.events.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e " +
            "JOIN e.category " +
            "WHERE ((:categoryIds) is null or e.category.id IN (:categoryIds)) " +
            "AND e.state = 'PUBLISHED' " +
            "AND (:paid is null or paid is :paid) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd")
    Page<Event> searchPublishedEvents(@Param("categoryIds") List<Long> categoryIds,
                                      @Param("paid") Boolean paid,
                                      @Param("rangeStart") LocalDateTime start,
                                      @Param("rangeEnd") LocalDateTime end,
                                      Pageable pageable);

    List<Event> findAllByInitiator_IdInAndCategory_IdInAndStateInAndPublishedOnAfterAndPublishedOnBefore(List<Long> userIds, List<Long> categories,
                                                                                                         List<State> stateList, LocalDateTime start,
                                                                                                         LocalDateTime end, PageRequest pageRequest);

    List<Event> findAllByInitiator_Id(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiator_Id(Long id, Long userId);

    Optional<Event> findByIdAndState(Long eventId, State state);

    Set<Event> findAllByIdIn(Set<Long> ids);

    Boolean existsByCategory_Id(Long catId);


}
