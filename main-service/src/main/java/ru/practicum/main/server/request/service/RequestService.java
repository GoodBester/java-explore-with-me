package ru.practicum.main.server.request.service;

import ru.practicum.main.server.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.server.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.server.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto create(Long userId, Long eventId);

    List<RequestDto> getCurrentUserRequests(Long userId);

    RequestDto cancel(Long userId, Long requestId);

    List<RequestDto> getRequestsByUserOfEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequests(Long userId, Long eventId,
                                                  EventRequestStatusUpdateRequest eventRequest);
}
