package ru.practicum.main.server.request.dto;

import lombok.Data;
import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatusToUpdate status;
}
