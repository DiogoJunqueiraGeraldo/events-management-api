package com.isiflix.events_management_api.domain.events;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.events.vos.PrettyNameVO;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> list(int page, int size) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("Page and size must be greater than zero.");
        }

        return this.eventRepository.list(page, size);
    }

    public Optional<EventDTO> findByPrettyName(String prettyName) {
        Objects.requireNonNull(prettyName, "Cannot find by 'prettyName' if it is null");
        final var event = eventRepository.findByPrettyName(PrettyNameVO.of(prettyName));
        return event.map(Event::toDTO);
    }
}
