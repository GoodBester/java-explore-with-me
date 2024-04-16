package ru.practicum.main.server.request.service.impl;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.main.server.events.model.Event;
import ru.practicum.main.server.request.dto.RequestDto;
import ru.practicum.main.server.request.model.Request;
import ru.practicum.main.server.users.model.User;

@Component
public class RequestConverter {
    private final ModelMapper modelMapper;
    private final Converter<User, Long> userLongConverter = (src) -> src.getSource().getId();
    private final Converter<Event, Long> eventLongConverter = (src) -> src.getSource().getId();


    public RequestConverter() {
        this.modelMapper = new ModelMapper();

        modelMapper.createTypeMap(Request.class, RequestDto.class)
                .addMappings(mapper -> mapper.using(userLongConverter).map(Request::getRequester, RequestDto::setRequester))
                .addMappings(mapper -> mapper.using(eventLongConverter).map(Request::getEvent, RequestDto::setEvent));
    }

    public RequestDto convertToDto(Request entity) {
        return modelMapper.map(entity, RequestDto.class);
    }
}
