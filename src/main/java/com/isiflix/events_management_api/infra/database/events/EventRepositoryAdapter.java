package com.isiflix.events_management_api.infra.database.events;

import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventRepository;
import com.isiflix.events_management_api.domain.events.vos.EventPrettyName;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class EventRepositoryAdapter implements EventRepository {
    public final static String PRETTY_NAME_UNIQUE_CONSTRAINT = "ems_events_pretty_name_key";
    private final JPAEventRepository jpaEventRepository;

    @Autowired
    public EventRepositoryAdapter(JPAEventRepository jpaEventRepository) {
        this.jpaEventRepository = jpaEventRepository;
    }

    @Override
    public Event saveAndCheckConstraints(Event event) {
        final var entity = EventMapper.toEntity(event);

        try {
            jpaEventRepository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            if (isPrettyNameConstraintViolation(e)) {
                throwPrettyNameAlreadyExists(event.getPrettyName());
            }

            throw e;
        }

        return EventMapper.fromEntity(entity);
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

    @Override
    public List<Event> list(int page, int size) {
        Pageable pageable = PageRequest.of(page -1, size, Sort.by("createdDatetime", "id").descending());
        return jpaEventRepository.findAll(pageable)
                .map(EventMapper::fromEntity)
                .toList();
    }

    @Override
    public Optional<Event> findByPrettyName(EventPrettyName eventPrettyName) {
        return jpaEventRepository.findByPrettyName(eventPrettyName.toString())
                .map(EventMapper::fromEntity);
    }
}
