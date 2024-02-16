package ru.prakticum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.prakticum.stats.server.model.Hit;
import ru.praktikum.stats.dto.model.StatDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query(value = "SELECT new ru.praktikum.stats.dto.model.StatDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit AS h " +
            "WHERE (h.timestamp between :start AND :end) AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC ")
    List<StatDto> getStatsByUrisWithUniqueIp(@Param("start") LocalDateTime timeStart, @Param("end") LocalDateTime timeEnd,
                                             @Param("uris") List<String> uris);

    @Query(value = "SELECT new ru.praktikum.stats.dto.model.StatDto(h.app, h.uri, COUNT(h.ip)) " +
            "FROM Hit AS h " +
            "WHERE (h.timestamp between :start AND :end) AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC ")
    List<StatDto> getStatsByUrisWithoutUniqueIp(@Param("start") LocalDateTime timeStart, @Param("end") LocalDateTime timeEnd,
                                                @Param("uris") List<String> uris);

    @Query(value = "SELECT new ru.praktikum.stats.dto.model.StatDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit AS h " +
            "WHERE (h.timestamp between :start AND :end) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC ")
    List<StatDto> getStatsWithoutUrisWithUniqueIp(@Param("start") LocalDateTime timeStart, @Param("end") LocalDateTime timeEnd);

    @Query(value = "SELECT new ru.praktikum.stats.dto.model.StatDto(h.app, h.uri, COUNT(h.ip)) " +
            "FROM Hit AS h " +
            "WHERE (h.timestamp between :start AND :end) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC ")
    List<StatDto> getStatsWithoutUrisWithoutUniqueIp(@Param("start") LocalDateTime timeStart, @Param("end") LocalDateTime timeEnd);


}
