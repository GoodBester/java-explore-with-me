package ru.practicum.main.server.comments.service;

import ru.practicum.main.server.comments.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, CommentDto commentDto, Long eventId);

    CommentDto updateComment(Long commentId, CommentDto commentDto);

    void deleteComment(Long userId, Long commentId);

    List<CommentDto> getAllByEventId(Long eventId);
}
