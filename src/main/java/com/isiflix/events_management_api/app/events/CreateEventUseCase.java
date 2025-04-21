package com.isiflix.events_management_api.app.events;

import com.isiflix.events_management_api.app.events.dtos.CreateEventDTO;
import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventFactory;
import com.isiflix.events_management_api.domain.events.EventRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class CreateEventUseCase {
    final static String PRETTY_NAME_UNIQUE_CONSTRAINT = "ems_events_pretty_name_key";
    private final EventRepository eventRepository;

    @Autowired
    public CreateEventUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventDTO createNewEvent(CreateEventDTO createEventDTO) {
        Event event = EventFactory.create(createEventDTO);

        try {
            Event persistedEvent = this.eventRepository.saveAndCheckConstraints(event);
            return persistedEvent.toDTO();
        } catch (DataIntegrityViolationException e) {
            if (isPrettyNameConstraintViolation(e)) {
                throwPrettyNameAlreadyExists(event.getPrettyName());
            }

            throw e;
        }
    }

    private void throwPrettyNameAlreadyExists(String eventPrettyName) {
        throw new BusinessRuleViolationException(
                ViolationCode.CONFLICT_PRETTY_NAME_ALREADY_EXISTS,
                "Can't create event because event's pretty name already exists",
                Map.of("prettyName", eventPrettyName)
        );
    }

    private boolean isPrettyNameConstraintViolation(DataIntegrityViolationException dataIntegrityViolationException) {
        return dataIntegrityViolationException.getCause() instanceof ConstraintViolationException constraintViolationException
                && Objects.equals(constraintViolationException.getConstraintName(), PRETTY_NAME_UNIQUE_CONSTRAINT);
    }
}
