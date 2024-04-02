package ru.practicum.main.server.users.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.server.users.dto.UserDto;
import ru.practicum.main.server.users.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<UserDto> findAllByIdIn(List<Long> ids, Pageable page);
}
