package ru.practicum.main.server.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.main.server.error.exception.IncorrectValueException;
import ru.practicum.main.server.error.exception.NotFoundException;
import ru.practicum.main.server.error.exception.UnsatisfactoryConditionException;
import ru.practicum.main.server.error.exception.ValidationException;
import ru.practicum.main.server.events.model.Event;
import ru.practicum.main.server.events.model.State;
import ru.practicum.main.server.events.repository.EventRepository;
import ru.practicum.main.server.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.server.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.server.request.dto.RequestDto;
import ru.practicum.main.server.request.dto.RequestStatusToUpdate;
import ru.practicum.main.server.request.model.Request;
import ru.practicum.main.server.request.model.RequestStatus;
import ru.practicum.main.server.request.repository.RequestRepository;
import ru.practicum.main.server.request.service.RequestService;
import ru.practicum.main.server.users.model.User;
import ru.practicum.main.server.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;
    private final ModelMapper mapper;


    @Override
    public RequestDto create(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь не найден."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Событие не найдено."));

        Request request = new Request(LocalDateTime.now(), event, user, RequestStatus.PENDING);

        Optional<Request> requests = requestRepository.findByRequester_IdAndEvent_id(userId, eventId);
        if (event.getInitiator().getId().equals(userId)) {
            throw new IncorrectValueException(HttpStatus.CONFLICT, "Ошибка. Событие организовано пользователем.");
        }
        if (requests.isPresent()) {
            throw new IncorrectValueException(HttpStatus.CONFLICT, "Ошибка. Запрос был отправлен ранее.");
        }
        if (!(event.getState().equals(State.PUBLISHED))) {
            throw new IncorrectValueException(HttpStatus.CONFLICT, "Ошибка. Событие не опубликовано.");
        }
        int limit = event.getParticipantLimit();
        if (limit != 0) {
            if (limit == event.getConfirmedRequests()) {
                throw new IncorrectValueException(HttpStatus.CONFLICT, "Ошибка. Достигнут лимит запросов на участие.");
            }
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }

        return mapper.map(requestRepository.save(request), RequestDto.class);
    }

    @Override
    public List<RequestDto> getCurrentUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь не найден."));

        List<Request> requests = requestRepository.findAllByRequester_IdAndEvent_InitiatorIdNot(userId, userId);

        return requests.stream()
                .map(r -> mapper.map(r, RequestDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto cancel(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь не найден."));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Заявка не найдена."));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Ошибка. Заявка не принадлежит пользователю.");
        }
        if (request.getStatus().equals(RequestStatus.REJECTED) || request.getStatus().equals(RequestStatus.CANCELED)) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Ошибка. Заявка была отклонена ранее.");
        }
        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Ошибка. Нельзя отменить подтвержденную заявку.");
        }
        request.setStatus(RequestStatus.CANCELED);

        return mapper.map(requestRepository.save(request), RequestDto.class);
    }

    @Override
    public List<RequestDto> getRequestsByUserOfEvent(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь не найден."));

        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Событие не найдено."));

        List<Request> requests = requestRepository.findAllByEvent_Initiator_IdAndEvent_Id(userId, eventId);

        return requests.stream()
                .map(r -> mapper.map(r, RequestDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с ID [" + userId + "] не найден."));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Событие с ID [" + eventId + "] не найдено."));

        if (event.getParticipantLimit() == 0 && !event.getRequestModeration()) {
            throw new UnsatisfactoryConditionException(HttpStatus.CONFLICT, "Подтверждение заявки не требуется.");
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new UnsatisfactoryConditionException(HttpStatus.CONFLICT, "Превышен лимит заявок.");
        }

        List<Long> requestIds = eventRequest.getRequestIds();
        RequestStatusToUpdate status = eventRequest.getStatus();

        List<Request> requests = requestIds.stream()
                .map(id -> requestRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Заявка не найдена.")))
                .collect(Collectors.toList());

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        List<Request> updatedRequests = new ArrayList<>();

        for (Request req : requests) {
            if (status == RequestStatusToUpdate.CONFIRMED && req.getStatus().equals(RequestStatus.PENDING)) {
                if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
                    req.setStatus(RequestStatus.REJECTED);
                    updatedRequests.add(req);
                    rejectedRequests.add(req);
                }
                req.setStatus(RequestStatus.CONFIRMED);
                updatedRequests.add(req);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                confirmedRequests.add(req);
            }
            if (status == RequestStatusToUpdate.REJECTED && req.getStatus().equals(RequestStatus.PENDING)) {
                req.setStatus(RequestStatus.REJECTED);
                updatedRequests.add(req);
                rejectedRequests.add(req);
            }
        }

        requestRepository.saveAll(updatedRequests);
        eventRepository.save(event);

        List<RequestDto> confirmedRequestDtos = confirmedRequests.stream()
                .map(r -> mapper.map(r, RequestDto.class))
                .collect(Collectors.toList());

        List<RequestDto> rejectedRequestDtos = rejectedRequests.stream()
                .map(r -> mapper.map(r, RequestDto.class))
                .collect(Collectors.toList());

        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();
        updateResult.setRejectedRequests(rejectedRequestDtos);
        updateResult.setConfirmedRequests(confirmedRequestDtos);

        return updateResult;
    }
}
