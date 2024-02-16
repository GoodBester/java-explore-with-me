package ru.prakticum.stats.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.prakticum.stats.server.service.StatService;
import ru.praktikum.stats.dto.model.HitDto;
import ru.praktikum.stats.dto.model.StatDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsController {
    private final StatService statService;


    @PostMapping("/hit")
    public HitDto addHit(@RequestBody HitDto hit) {
        hit.setTimestamp(LocalDateTime.now());
        return statService.addHit(hit);
    }

    @GetMapping("/stats")
    public List<StatDto> getStats(@RequestParam String start,
                                  @RequestParam String end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(defaultValue = "false") boolean unique) {
        return statService.getStats(start, end, uris, unique);
    }


}
