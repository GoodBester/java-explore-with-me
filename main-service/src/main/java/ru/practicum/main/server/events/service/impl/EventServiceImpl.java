package ru.practicum.main.server.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.main.server.categories.model.Category;
import ru.practicum.main.server.categories.repository.CategoryRepository;
import ru.practicum.main.server.error.exception.IncorrectValueException;
import ru.practicum.main.server.error.exception.NotFoundException;
import ru.practicum.main.server.error.exception.ValidationException;
import ru.practicum.main.server.events.dto.EventFullDto;
import ru.practicum.main.server.events.dto.EventShortDto;
import ru.practicum.main.server.events.dto.NewEventDto;
import ru.practicum.main.server.events.model.Event;
import ru.practicum.main.server.events.model.Location;
import ru.practicum.main.server.events.model.Sort;
import ru.practicum.main.server.events.model.State;
import ru.practicum.main.server.events.repository.EventRepository;
import ru.practicum.main.server.events.repository.LocationRepository;
import ru.practicum.main.server.events.service.EventService;
import ru.practicum.main.server.events.dto.UpdateEventAdminRequest;
import ru.practicum.main.server.events.dto.UpdateEventUserRequest;
import ru.practicum.main.server.request.model.StateActionAdmin;
import ru.practicum.main.server.request.model.StateActionUser;
import ru.practicum.main.server.users.model.User;
import ru.practicum.main.server.users.repository.UserRepository;
import ru.practicum.stats.client.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsClient statsClient = new StatsClient("http://localhost:9090", new RestTemplateBuilder());


    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с ID " + userId + " не найден."));

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException(HttpStatus.CONFLICT, "Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
        }
        if (newEventDto.getParticipantLimit() < 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Колличество участников не может быть отрицательным");
        }
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Категория с ID " + newEventDto.getCategory() + " не найдена."));
        Event forSave = mapper.map(newEventDto, Event.class);
        forSave.setState(State.PENDING);
        forSave.setConfirmedRequests(0L);
        forSave.setCreatedOn(LocalDateTime.now());
        forSave.setCategory(category);
        forSave.setInitiator(user);

        return mapper.map(eventRepository.save(forSave), EventFullDto.class);

    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Событие не найдено."));

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь не найден.");
        }

        if (event.getState().equals(State.CANCELED) || event.getState().equals(State.PENDING)) {
            if (updateEventUserRequest.getEventDate() != null && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException(HttpStatus.CONFLICT, "Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
            }

            if (StateActionUser.SEND_TO_REVIEW == updateEventUserRequest.getStateAction()) {
                event.setState(State.PENDING);
            }
            if (StateActionUser.CANCEL_REVIEW == updateEventUserRequest.getStateAction()) {
                event.setState(State.CANCELED);
            }
        } else {
            throw new IncorrectValueException(HttpStatus.CONFLICT, "Событие не отменено или в состоянии модерации");
        }

        event.setAnnotation(Objects.requireNonNullElse(updateEventUserRequest.getAnnotation(), event.getAnnotation()));

        event.setCategory(updateEventUserRequest.getCategory() == null
                ? event.getCategory()
                : categoryRepository.findById(updateEventUserRequest.getCategory())
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Категория с не найдена.")));

        event.setDescription(Objects.requireNonNullElse(updateEventUserRequest.getDescription(),
                event.getDescription()));

        event.setEventDate(Objects.requireNonNullElse(updateEventUserRequest.getEventDate(),
                event.getEventDate()));

        event.setLocation(updateEventUserRequest.getLocation() == null
                ? event.getLocation()
                : locationRepository.findByLonAndLat(updateEventUserRequest.getLocation().getLon(), updateEventUserRequest.getLocation().getLat())
                .orElse(new Location(null, updateEventUserRequest.getLocation().getLat(), updateEventUserRequest.getLocation().getLon())));

        event.setPaid(Objects.requireNonNullElse(updateEventUserRequest.getPaid(), event.getPaid()));
        event.setParticipantLimit(Objects.requireNonNullElse(updateEventUserRequest.getParticipantLimit(),
                event.getParticipantLimit()));
        event.setRequestModeration(Objects.requireNonNullElse(updateEventUserRequest.getRequestModeration(),
                event.getRequestModeration()));
        event.setTitle(Objects.requireNonNullElse(updateEventUserRequest.getTitle(), event.getTitle()));


        return mapper.map(eventRepository.save(event), EventFullDto.class);
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Событие не найдено."));

        if ((updateEventAdminRequest.getEventDate() != null)
                && (updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now()))) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "дата начала изменяемого события должна быть не ранее чем за час от даты публикации.");
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction() == StateActionAdmin.PUBLISH_EVENT) {
                if (event.getState().equals(State.PENDING)) {
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                } else {
                    throw new IncorrectValueException(HttpStatus.CONFLICT, "Cобытие можно публиковать, только если оно в состоянии ожидания публикации.");
                }
            }
            if (updateEventAdminRequest.getStateAction() == StateActionAdmin.REJECT_EVENT) {
                if (event.getState().equals(State.PUBLISHED)) {
                    throw new IncorrectValueException(HttpStatus.CONFLICT, "Cобытие можно отклонить, только если оно еще не опубликовано.");
                }
                event.setState(State.CANCELED);
            }
        }

        event.setAnnotation(Objects.requireNonNullElse(updateEventAdminRequest.getAnnotation(), event.getAnnotation()));

        event.setCategory(updateEventAdminRequest.getCategory() == null
                ? event.getCategory()
                : categoryRepository.findById(updateEventAdminRequest.getCategory())
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Категория не найдена.")));

        event.setDescription(Objects.requireNonNullElse(updateEventAdminRequest.getDescription(),
                event.getDescription()));

        event.setEventDate(Objects.requireNonNullElse(updateEventAdminRequest.getEventDate(),
                event.getEventDate()));

        event.setLocation(updateEventAdminRequest.getLocation() == null
                ? event.getLocation()
                : locationRepository.findByLonAndLat(updateEventAdminRequest.getLocation().getLon(), updateEventAdminRequest.getLocation().getLat())
                .orElse(new Location(null, updateEventAdminRequest.getLocation().getLat(), updateEventAdminRequest.getLocation().getLon())));

        event.setPaid(Objects.requireNonNullElse(updateEventAdminRequest.getPaid(), event.getPaid()));
        event.setParticipantLimit(Objects.requireNonNullElse(updateEventAdminRequest.getParticipantLimit(),
                event.getParticipantLimit()));
        event.setRequestModeration(Objects.requireNonNullElse(updateEventAdminRequest.getRequestModeration(),
                event.getRequestModeration()));
        event.setTitle(Objects.requireNonNullElse(updateEventAdminRequest.getTitle(), event.getTitle()));

        return mapper.map(eventRepository.save(event), EventFullDto.class);
    }

    @Override
    public List<EventShortDto> getAllEventPublic(String text, List<Long> categoriesIds, Boolean paid, String
            rangeStart, String rangeEnd, Boolean onlyAvailable, Sort sort, Integer from, Integer size, HttpServletRequest
                                                         request) {

        LocalDateTime start = null;
        LocalDateTime end = null;

        if (rangeStart != null && rangeEnd != null) {
            start = LocalDateTime.parse(rangeStart, dateFormatter);
            end = LocalDateTime.parse(rangeEnd, dateFormatter);
            if (start.isAfter(end)) {
                throw new ValidationException(HttpStatus.BAD_REQUEST, "Даты некорректны.");
            }
        } else {
            if (rangeStart == null && rangeEnd == null) {
                start = LocalDateTime.now();
                end = LocalDateTime.now().plusYears(10);
            } else {
                if (rangeStart == null) {
                    start = LocalDateTime.now();
                }
                if (rangeEnd == null) {
                    end = LocalDateTime.now();
                }
            }
        }

        final PageRequest pageRequest = PageRequest.of(from / size, size,
                org.springframework.data.domain.Sort.by(Sort.EVENT_DATE.equals(sort) ? "eventDate" : "views"));

        List<Event> eventEntities = eventRepository.searchPublishedEvents(categoriesIds, paid, start, end, pageRequest)
                .getContent();

        statsClient.addHit("ewm-service", request.getRequestURI(), request.getRemoteAddr());

        if (eventEntities.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> eventIds = eventEntities.stream().map(Event::getId).collect(Collectors.toSet());

        Map<Long, Long> statsMap = statsClient.getStatsForViews(eventIds);

        List<EventShortDto> events = eventEntities
                .stream()
                .map(event -> mapper.map(event, EventShortDto.class))
                .collect(Collectors.toList());

        events.forEach(eventShortDto ->
                eventShortDto.setViews(statsMap.getOrDefault(eventShortDto.getId(), 0L)));

        return events;
    }

    @Override
    public List<EventFullDto> getAllEventAdmin(List<Long> userIds, List<String> states, List<Long> categories, String rangeStart,
                                               String rangeEnd, Integer from, Integer size, HttpServletRequest request) {
        final PageRequest pageRequest = PageRequest.of(from / size, size);

        if (states == null && rangeStart == null && rangeEnd == null) {
            return eventRepository.findAll(pageRequest)
                    .stream()
                    .map(event -> mapper.map(event, EventFullDto.class))
                    .collect(Collectors.toList());
        }
        List<State> stateList;
        if (states == null) {
            stateList = Collections.emptyList();
        } else {
            stateList = states.stream().map(State::valueOf).collect(Collectors.toList());
        }

        LocalDateTime start;
        if (rangeStart != null && !rangeStart.isEmpty()) {
            start = LocalDateTime.parse(rangeStart, dateFormatter);
        } else {
            start = LocalDateTime.now().plusYears(100);
        }

        LocalDateTime end;
        if (rangeEnd != null && !rangeEnd.isEmpty()) {
            end = LocalDateTime.parse(rangeEnd, dateFormatter);
        } else {
            end = LocalDateTime.now().plusYears(100);
        }

        if (!userIds.isEmpty() && !stateList.isEmpty() && !categories.isEmpty()) {
            return getEventDtoListWithAllParameters(userIds, categories, stateList, start, end, pageRequest).stream().map(e -> mapper.map(e, EventFullDto.class)).collect(Collectors.toList());
        }
        if (userIds.isEmpty() && !categories.isEmpty()) {
            return getEventDtoListWithAllParameters(userIds, categories, stateList, start, end, pageRequest).stream().map(e -> mapper.map(e, EventFullDto.class)).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<EventShortDto> getAllEventByUser(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь не найден.");
        }

        return eventRepository.findAllByInitiator_Id(userId, PageRequest.of(from, size)).stream().map(event -> mapper.map(event, EventShortDto.class)).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь не найден.");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Событие не найдено.");
        }

        return mapper.map(eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Событие не существует.")), EventFullDto.class);
    }

    @Override
    public EventFullDto getByIdEventPublic(Long eventId, HttpServletRequest httpRequest) {

        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Событие не найдено."));

        statsClient.addHit("ewm-service", httpRequest.getRequestURI(), httpRequest.getRemoteAddr());

        Long views = statsClient.getStats(eventId);

        EventFullDto eventFullDto = mapper.map(event, EventFullDto.class);
        eventFullDto.setViews(views);
        return eventFullDto;
    }

    private List<EventFullDto> getEventDtoListWithAllParameters(List<Long> userIds, List<Long> categories,
                                                                List<State> stateList, LocalDateTime start,
                                                                LocalDateTime end, PageRequest pageRequest) {

        List<EventFullDto> events = eventRepository.findAllByInitiator_IdInAndCategory_IdInAndStateInAndPublishedOnAfterAndPublishedOnBefore(userIds, categories,
                stateList, start, end,
                pageRequest).stream().map(e -> mapper.map(e, EventFullDto.class)).collect(Collectors.toList());

        Set<Long> eventIds = events.stream().map(EventFullDto::getId).collect(Collectors.toSet());

        Map<Long, Long> viewStatsMap = statsClient.getStatsForViews(eventIds);

        events.forEach(eventFullDto ->
                eventFullDto.setViews(viewStatsMap.getOrDefault(eventFullDto.getId(), 0L)));

        return events;
    }
}
