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
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatusToUpdate status;
}
