package ru.practicum.main.server.users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.server.users.dto.NewUserDto;
import ru.practicum.main.server.users.dto.UserDto;
import ru.practicum.main.server.users.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody NewUserDto newUserDto) {
        return userService.createUser(newUserDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> findAllUsers(@RequestParam(value = "ids", required = false) List<Long> ids,
                                      @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                      @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {

        return userService.findAllUsers(ids, PageRequest.of(from, size));
    }
}
