package com.isiflix.events_management_api.app.events;

import com.isiflix.events_management_api.app.events.dtos.CreateEventDTO;
import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.domain.events.EventFactory;
import com.isiflix.events_management_api.domain.events.EventRepository;
import com.isiflix.events_management_api.domain.events.vos.PrettyNameVO;
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

    public EventDTO createNewEvent(CreateEventDTO createEventDTO) {
        // Avoid sequence exhaustion: pre-check to skip ID allocation when input is invalid
        PrettyNameVO prettyName = PrettyNameVO.of(createEventDTO.name());
        if(this.eventRepository.findByPrettyName(prettyName).isPresent()) {
            throwPrettyNameAlreadyExists(prettyName);
        }

        // Sequence numbers are non-transactional – allocate only when needed
        Long id = this.eventRepository.nextId();
        final var event = EventFactory.create(id, createEventDTO);

        try {
            // Attempt insert, rely on DB constraint as last-resort safety net
            this.eventRepository.save(event);
        } catch (DataIntegrityViolationException e) {
            // Graceful fallback for race condition on pretty name
            if(isPrettyNameConstraintViolation(e)) {
                throwPrettyNameAlreadyExists(event.getPrettyName());
            }

            // Unexpected integrity violation
            throw e;
        }

        return event.toDTO();
    }

    private void throwPrettyNameAlreadyExists(PrettyNameVO prettyName) {
        throw new BusinessRuleViolationException(
                ViolationCode.CONFLICT_PRETTY_NAME_ALREADY_EXISTS,
                "Can't create event because event's pretty name already exists",
                Map.of("prettyName", prettyName.prettyName())
        );
    }

    private boolean isPrettyNameConstraintViolation(DataIntegrityViolationException dataIntegrityViolationException) {
        return dataIntegrityViolationException.getCause() instanceof ConstraintViolationException constraintViolationException
                && Objects.equals(constraintViolationException.getConstraintName(), PRETTY_NAME_UNIQUE_CONSTRAINT);
    }
}
