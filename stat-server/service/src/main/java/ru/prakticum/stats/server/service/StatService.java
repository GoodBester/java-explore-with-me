package ru.prakticum.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.prakticum.stats.server.exception.ValidationException;
import ru.prakticum.stats.server.model.Hit;
import ru.prakticum.stats.server.repository.HitRepository;
import ru.praktikum.stats.dto.model.HitDto;
import ru.praktikum.stats.dto.model.NewHitDto;
import ru.praktikum.stats.dto.model.StatDto;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatService {

    private final HitRepository hitRepository;
    private final ModelMapper mapper;
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public HitDto addHit(NewHitDto hit) {
        Hit ent = mapper.map(hit, Hit.class);
        return mapper.map(hitRepository.save(ent), HitDto.class);
    }

    public List<StatDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startDate;
        LocalDateTime endDate;

        try {
            startDate = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), dateTimeFormatter);
            endDate = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), dateTimeFormatter);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Incorrect date format.");
        }
        if (startDate.isAfter(endDate)) {
            throw new ru.prakticum.stats.server.exception.ValidationException("Start and end times are not correct.");
        }

        if (uris != null && !uris.isEmpty()) {
            uris.replaceAll(s -> s.replace("[", ""));
            uris.replaceAll(s -> s.replace("]", ""));
            if (unique) {
                uris.replaceAll(s -> s.replace("[", ""));
                uris.replaceAll(s -> s.replace("]", ""));
                return hitRepository.getStatsByUrisWithUniqueIp(startDate, endDate, uris);
            } else {
                uris.replaceAll(s -> s.replace("[", ""));
                uris.replaceAll(s -> s.replace("]", ""));
                return hitRepository.getStatsByUrisWithoutUniqueIp(startDate, endDate, uris);
            }
        }
        if (unique) {
            return hitRepository.getStatsWithoutUrisWithUniqueIp(startDate, endDate);
        } else {
            return hitRepository.getStatsWithoutUrisWithoutUniqueIp(startDate, endDate);
        }
    }


}
