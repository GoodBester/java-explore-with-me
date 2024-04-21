package ru.practicum.main.server.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.server.comments.dto.CommentDto;
import ru.practicum.main.server.comments.service.CommentService;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/users/{userId}/events/{eventId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long eventId, @PathVariable Long userId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return commentService.createComment(userId, commentDto, eventId);
    }

    @DeleteMapping("/admin/comments/{userId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId, @PathVariable Long commentId) {
        commentService.deleteComment(userId, commentId);
    }

    @PatchMapping("/admin/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Long commentId, @Valid @RequestBody CommentDto commentDto) {
        return commentService.updateComment(commentId, commentDto);
    }

    @GetMapping("/events/{eventId}/comments/all")
    public List<CommentDto> getAllEventsComments(@PathVariable Long eventId) {
        return commentService.getAllByEventId(eventId);
    }
}
