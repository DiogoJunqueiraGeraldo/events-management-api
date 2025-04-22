package com.isiflix.events_management_api.app.events;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventRepository;
import com.isiflix.events_management_api.domain.events.vos.EventPrettyName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class FindEventUseCase {
    private final EventRepository eventRepository;

    @Autowired
    public FindEventUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Optional<EventDTO> findByPrettyName(String prettyName) {
        return eventRepository.findByPrettyName(EventPrettyName.of(prettyName))
                .map(Event::toDTO);
    }

    public EventDTO findEventByPrettyNameOrThrow(String prettyName) {
        return findByPrettyName(prettyName)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        ViolationCode.EVENT_NOT_FOUND_BY_PRETTY_NAME,
                        "Event not found by 'prettyName'",
                        Map.of("prettyName", prettyName)
                ));
    }
}
