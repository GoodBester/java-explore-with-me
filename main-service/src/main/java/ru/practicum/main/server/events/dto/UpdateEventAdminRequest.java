package ru.practicum.main.server.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.main.server.events.model.Location;
import ru.practicum.main.server.request.model.StateActionAdmin;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;


@Data
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateActionAdmin stateAction;
    @Size(min = 3, max = 120)
    private String title;
}
