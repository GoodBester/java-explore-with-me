package ru.practicum.main.server.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.server.events.model.Location;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByLonAndLat(Float lon, Float lat);
}
