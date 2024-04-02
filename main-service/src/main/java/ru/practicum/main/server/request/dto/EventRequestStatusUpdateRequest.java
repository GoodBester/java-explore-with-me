package ru.practicum.main.server.request.dto;

import lombok.*;
import ru.practicum.main.server.request.model.RequestStatus;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatusToUpdate status;
}
