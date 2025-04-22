package com.isiflix.events_management_api.app.events;

import com.isiflix.events_management_api.app.events.dtos.CreateEventDTO;
import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.events.EventFactory;
import com.isiflix.events_management_api.domain.events.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateEventUseCase {
    private final EventRepository eventRepository;

    @Autowired
    public CreateEventUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventDTO createNewEvent(CreateEventDTO createEventDTO) {
        final var event = EventFactory.create(createEventDTO);
        return this.eventRepository.saveAndCheckConstraints(event).toDTO();
    }
}
