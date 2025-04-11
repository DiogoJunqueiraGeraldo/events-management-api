package com.isiflix.events_management_api.app.events;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListEventsUseCase {
    private final EventRepository eventRepository;

    @Autowired
    public ListEventsUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventDTO> list(int page, int size) {
        return this.eventRepository.list(page, size)
                .stream()
                .map(Event::toDTO)
                .toList();
    }
}
