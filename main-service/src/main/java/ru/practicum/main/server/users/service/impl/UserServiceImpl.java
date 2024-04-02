package ru.practicum.main.server.users.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.main.server.error.exception.NotFoundException;
import ru.practicum.main.server.users.dto.NewUserDto;
import ru.practicum.main.server.users.dto.UserDto;
import ru.practicum.main.server.users.model.User;
import ru.practicum.main.server.users.repository.UserRepository;
import ru.practicum.main.server.users.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    @Override
    public UserDto createUser(NewUserDto userDto) {
        User user = mapper.map(userDto, User.class);
        return mapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    public String deleteUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь не найден или недоступен"));
        return "Пользователь удален";
    }

    @Override
    public List<UserDto> findAllUsers(List<Long> ids, Pageable pageable) {
        return userRepository.findAllByIdIn(ids, pageable);
    }
}
