package ru.practicum.main.server.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventRequestStatusUpdateResult {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
