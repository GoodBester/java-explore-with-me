package ru.practicum.main.server.events.service;

import ru.practicum.main.server.events.dto.EventFullDto;
import ru.practicum.main.server.events.dto.EventShortDto;
import ru.practicum.main.server.events.dto.NewEventDto;
import ru.practicum.main.server.events.model.Sort;
import ru.practicum.main.server.request.dto.UpdateEventAdminRequest;
import ru.practicum.main.server.request.dto.UpdateEventUserRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getAllEventPublic(String text, List<Long> categoriesIds, Boolean paid, String rangeStart,
                                     String rangeEnd, Boolean onlyAvailable, Sort sort, Integer from,
                                     Integer size, HttpServletRequest request);

    List<EventFullDto> getAllEventAdmin(List<Long> userIds, List<String> states, List<Long> categories,
                                   String rangeStart, String rangeEnd, Integer from, Integer size,
                                   HttpServletRequest request);

    List<EventShortDto> getAllEventByUser(Long userId, Integer from, Integer size);

    EventFullDto getEventByUser(Long userId, Long eventId);

    EventFullDto getByIdEventPublic(Long eventId, HttpServletRequest httpRequest);
}
