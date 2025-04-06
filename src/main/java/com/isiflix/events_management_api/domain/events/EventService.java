package com.isiflix.events_management_api.domain.events;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.domain.events.vos.PrettyNameVO;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void save(Event event) {
        Objects.requireNonNull(event, "Cannot save 'event' that is null");

        if(eventRepository.findByPrettyName(event.getPrettyName()).isPresent()) {
            throw new BusinessRuleViolationException(
                    ViolationCode.CONFLICT_PRETTY_NAME_ALREADY_EXISTS,
                    "Can't create event because event's pretty name already exists",
                    Map.of("prettyName", event.getPrettyName().prettyName())
            );
        }

        Long id = eventRepository.save(event);
        event.setId(id);
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
