package ru.practicum.main.server.comments.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.main.server.comments.dto.CommentDto;
import ru.practicum.main.server.comments.model.Comment;
import ru.practicum.main.server.comments.repository.CommentRepository;
import ru.practicum.main.server.comments.service.CommentService;
import ru.practicum.main.server.error.exception.NotFoundException;
import ru.practicum.main.server.events.model.Event;
import ru.practicum.main.server.events.repository.EventRepository;
import ru.practicum.main.server.request.repository.RequestRepository;
import ru.practicum.main.server.users.model.User;
import ru.practicum.main.server.users.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ModelMapper mapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;


    @Override
    public CommentDto createComment(Long userId, CommentDto commentDto, Long eventId) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isEmpty()) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь не участвовал событии");
        }
        Comment comment = mapper.map(commentDto, Comment.class);
        comment.setAuthor(user);
        comment.setEvent(event);

        return mapper.map(commentRepository.save(comment), CommentDto.class);
    }

    @Override
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = checkComment(commentId);

        comment.setText(commentDto.getText());
        comment.setCreated(commentDto.getCreated());

        return mapper.map(commentRepository.save(comment), CommentDto.class);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        checkUser(userId);
        checkComment(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getAllByEventId(Long eventId) {
        checkEvent(eventId);
        return commentRepository.findAllByEvent_Id(eventId)
                .stream()
                .map(c -> mapper.map(c, CommentDto.class))
                .collect(Collectors.toList());
    }

    private User checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь найден");
        }
        return userRepository.findById(userId).get();
    }

    private Event checkEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Событие не найдено");
        }
        return eventRepository.findById(eventId).get();
    }

    private Comment checkComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Комментарий не найден");
        }
        return commentRepository.findById(commentId).get();
    }
}
