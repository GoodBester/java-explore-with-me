package ru.practicum.main.server.compilations.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.server.compilations.dto.CompilationDto;
import ru.practicum.main.server.compilations.model.Compilation;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    List<CompilationDto> findAllByPinned(Boolean pinned, Pageable page);
}
