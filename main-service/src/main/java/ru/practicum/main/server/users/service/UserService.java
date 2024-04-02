package ru.practicum.main.server.users.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.server.users.dto.NewUserDto;
import ru.practicum.main.server.users.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserDto userDto);

    String deleteUser(Long userId);

    List<UserDto> findAllUsers(List<Long> ids, Pageable pageable);
}
