package ru.practicum.main.server.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.server.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.server.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.server.request.dto.RequestDto;
import ru.practicum.main.server.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestController {

    private final RequestService requestService;

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<RequestDto> getAllRequests(@PathVariable Long userId, @PathVariable Long eventId) {

        return requestService.getRequestsByUserOfEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequests(@PathVariable Long userId,
                                                         @PathVariable Long eventId,
                                                         @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        return requestService.updateRequests(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @PostMapping("/users/{userId}/requests")
    public RequestDto create(@PathVariable(name = "userId") Long userId,
                             @Valid @RequestParam(name = "eventId") Long eventId) {

        return requestService.create(userId, eventId);
    }

    @GetMapping("/users/{userId}/requests")
    public List<RequestDto> getCurrentUserRequests(@PathVariable(name = "userId") Long userId) {

        return requestService.getCurrentUserRequests(userId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public RequestDto cancel(@PathVariable(name = "userId") Long userId, @PathVariable Long requestId) {

        return requestService.cancel(userId, requestId);
    }

}
