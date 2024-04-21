package ru.practicum.main.server.request.dto;

import lombok.Data;
import java.util.List;

@Data
public class EventRequestStatusUpdateResult {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
