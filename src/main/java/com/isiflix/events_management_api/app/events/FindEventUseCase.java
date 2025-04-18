package com.isiflix.events_management_api.app.events;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventRepository;
import com.isiflix.events_management_api.domain.events.vos.PrettyNameVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FindEventUseCase {
    private final EventRepository eventRepository;

    @Autowired
    public FindEventUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Optional<EventDTO> findByPrettyName(String prettyName) {
        return eventRepository.findByPrettyName(PrettyNameVO.of(prettyName))
                .map(Event::toDTO);
    }
}
