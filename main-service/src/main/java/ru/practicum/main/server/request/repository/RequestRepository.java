package ru.practicum.main.server.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.server.request.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findByRequester_IdAndEvent_id(Long userId, Long eventId);

    List<Request> findAllByRequester_IdAndEvent_InitiatorIdNot(Long userId1, Long userId2);

    List<Request> findAllByEvent_Initiator_IdAndEvent_Id(Long userId, Long eventId);
}
