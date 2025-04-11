package com.isiflix.events_management_api.app.events;

import com.isiflix.events_management_api.app.events.dtos.CreateEventDTO;
import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.domain.events.EventFactory;
import com.isiflix.events_management_api.domain.events.EventRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class CreateEventUseCase {
    final static String PRETTY_NAME_UNIQUE_CONSTRAINT = "ems_events_pretty_name_key";
    private final EventRepository eventRepository;

    @Autowired
    public CreateEventUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventDTO createNewEvent(CreateEventDTO createNewEvent) {
        Long id = this.eventRepository.nextId();
        final var event = EventFactory.create(id, createNewEvent);

        try {
            this.eventRepository.save(event);
        } catch (DataIntegrityViolationException e) {
            if(isPrettyNameConstraintViolation(e)) {
                throw new BusinessRuleViolationException(
                        ViolationCode.CONFLICT_PRETTY_NAME_ALREADY_EXISTS,
                        "Can't create event because event's pretty name already exists",
                        Map.of("prettyName", event.getPrettyName().prettyName())
                );
            }

            throw e;
        }

        return event.toDTO();
    }

    public boolean isPrettyNameConstraintViolation(DataIntegrityViolationException dataIntegrityViolationException) {
        return dataIntegrityViolationException.getCause() instanceof ConstraintViolationException constraintViolationException
                && Objects.equals(constraintViolationException.getConstraintName(), PRETTY_NAME_UNIQUE_CONSTRAINT);
    }
}
