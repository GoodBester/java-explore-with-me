package ru.practicum.stats.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.praktikum.stats.dto.NewHitDto;
import ru.praktikum.stats.dto.model.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StatsClient extends BaseClient {

    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeReference<List<StatDto>> mapType = new TypeReference<>() {
    };
    private final TypeReference<StatDto> dtoType = new TypeReference<>() {
    };

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addHit(String app, String uri, String ip, LocalDateTime timestamp) {
        NewHitDto hitDto = new NewHitDto(app, uri, ip, timestamp);
        return post("/hit", hitDto);
    }


    public Long getStats(Long eventId) {
        Map<String, Object> parameters = Map.of(
                "start", LocalDateTime.of(1900, 1, 1, 1, 1),
                "end", LocalDateTime.of(2100, 1, 1, 1, 1),
                "uris", List.of("/events/" + eventId),
                "unique", true
        );
        ResponseEntity<Object> response = get("/stats?start={start}&end={end}&uris[]={uris}&unique={unique}", parameters);
        List<StatDto> mapped;
        if (response.hasBody()) {
            mapped = mapper.convertValue(response, mapType);
        } else {
            mapped = Collections.emptyList();
        }

        if(mapped.isEmpty()){
            return 0L;
        } else {
            return mapped.get(0).getHits();
        }


    }

    public Map<Long, Long> getStatsForViews(Set<Long> uris) {
        Map<String, Object> parameters = Map.of(
                "start", LocalDateTime.of(1900, 1, 1, 1, 1),
                "end", LocalDateTime.of(2100, 1, 1, 1, 1),
                "uris", (uris.stream().map(id -> "/events/" + id).collect(Collectors.toList())),
                "unique", false
        );
        ResponseEntity<Object> response = get("/stats?start={start}&end={end}&uris[]={uris}&unique={unique}", parameters);

        if (response.hasBody()) {
            List<StatDto> mapped = mapper.convertValue(response, mapType);
            return mapped.stream().collect(Collectors.toMap(s -> Long.parseLong(s.getUri().substring(s.getUri().lastIndexOf("/") + 1)), StatDto::getHits));
        } else {
            return Collections.emptyMap();
        }
    }

}
