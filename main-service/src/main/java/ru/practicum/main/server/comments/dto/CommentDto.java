package ru.practicum.main.server.comments.dto;

import lombok.Data;
import ru.practicum.main.server.events.dto.EventShortDto;
import ru.practicum.main.server.users.dto.UserDto;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    @NotNull
    @Size(max = 3000)
    private String text;
    private UserDto author;
    private EventShortDto event;
    private LocalDateTime created;
}
