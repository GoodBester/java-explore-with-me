package ru.practicum.main.server.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.server.events.dto.EventFullDto;
import ru.practicum.main.server.events.dto.EventShortDto;
import ru.practicum.main.server.events.dto.NewEventDto;
import ru.practicum.main.server.events.model.Sort;
import ru.practicum.main.server.events.service.EventService;
import ru.practicum.main.server.request.dto.UpdateEventAdminRequest;
import ru.practicum.main.server.request.dto.UpdateEventUserRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventsController {

    private final EventService eventService;

    @GetMapping("/events")
    public List<EventShortDto> getAllEventPublic(@RequestParam(required = false) String text,
                                                 @RequestParam(required = false) List<Long> categories,
                                                 @RequestParam(required = false) Boolean paid,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String rangeStart,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String rangeEnd,
                                                 @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                                 @RequestParam(required = false) Sort sort,
                                                 @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(required = false, defaultValue = "10") @Positive Integer size,
                                                 HttpServletRequest request) {

        return eventService.getAllEventPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size, request);
    }
    @GetMapping("/admin/events")
    public List<EventFullDto> getAllEventAdmin(@RequestParam(name = "users", required = false) List<Long> userIds,
                                               @RequestParam(name = "states", required = false) List<String> states,
                                               @RequestParam(name = "categories", required = false) List<Long> categories,
                                               @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                               @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                               @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @RequestParam(name = "size", defaultValue = "10") Integer size,
                                               HttpServletRequest request) {

        return eventService.getAllEventAdmin(userIds, states, categories, rangeStart, rangeEnd, from, size, request);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {
        return eventService.getByIdEventPublic(id, request);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getUserEvents(@PathVariable Long id,
                                             @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return eventService.getAllEventByUser(id, from, size);
    }

    @PostMapping("/users/{userId}/events")
    public EventFullDto addEvent(@PathVariable Long id,
                                 @RequestBody NewEventDto newEventDto) {
        return eventService.addEvent(id, newEventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getEventByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEventByUser(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable Long userId, @PathVariable Long eventId,
                                          @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEventByUser(userId, eventId, updateEventUserRequest);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId, @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }

}
