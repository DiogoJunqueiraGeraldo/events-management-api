package com.isiflix.events_management_api.infra.database.events;

import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventFactory;
import com.isiflix.events_management_api.domain.events.vos.EventPeriodTest;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventRepositoryAdapterTest {
    @Mock
    JPAEventRepository jpaEventRepository;

    @Mock
    ConstraintViolationException constraintViolationException;

    @InjectMocks
    EventRepositoryAdapter eventRepositoryAdapter;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    static Event event;

    @BeforeAll
    public static void setUp() {
        event = EventFactory.fromRaw(
                1L,
                "Foo Bar",
                "foo-bar",
                "online",
                BigDecimal.ZERO,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(EventPeriodTest.VALID_EVENT_DURATION_IN_MINUTES)
        );
    }

    Event mockForId(int id) {
        return EventFactory.fromRaw(
                (long) id,
                "Foo Bar",
                "foo-bar",
                "online",
                BigDecimal.ZERO,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(EventPeriodTest.VALID_EVENT_DURATION_IN_MINUTES)
        );
    }

    @Test
    public void shouldFlushForCheckConstraintsOnSave() {
        when(jpaEventRepository.saveAndFlush(any(EventEntity.class))).thenReturn(EventMapper.toEntity(event));
        eventRepositoryAdapter.saveAndCheckConstraints(event);
        verify(jpaEventRepository, times(1)).saveAndFlush(any(EventEntity.class));
    }

    @Test
    public void shouldThrowBusinessExceptionWhenPrettyNameConstraintViolationIsThrown() {
        when(constraintViolationException.getConstraintName()).thenReturn(EventRepositoryAdapter.PRETTY_NAME_UNIQUE_CONSTRAINT);
        final var dataIntegrityException = new DataIntegrityViolationException("Foo", constraintViolationException);
        when(jpaEventRepository.saveAndFlush(any(EventEntity.class))).thenThrow(dataIntegrityException);

        final var exception = catchThrowable(() -> eventRepositoryAdapter.saveAndCheckConstraints(event));

        assertThat(exception)
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage( "Can't create event because event's pretty name already exists");

        final var businessRuleViolationException = (BusinessRuleViolationException) exception;
        assertThat(businessRuleViolationException.getViolationCode()).isEqualTo(ViolationCode.CONFLICT_PRETTY_NAME_ALREADY_EXISTS);
    }

    @Test
    public void shouldThrowExceptionIfIsNotPrettyNameConstraintViolation() {
        when(constraintViolationException.getConstraintName()).thenReturn("another-constraint");
        final var dataIntegrityException = new DataIntegrityViolationException("Foo", constraintViolationException);
        when(jpaEventRepository.saveAndFlush(any(EventEntity.class))).thenThrow(dataIntegrityException);

        assertThatThrownBy(() -> eventRepositoryAdapter.saveAndCheckConstraints(event))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage( "Foo");
    }

    @Test
    public void shouldThrowExceptionIfIsNotCausedByConstraintViolation() {
        final var dataIntegrityException = new DataIntegrityViolationException("Foo", new NullPointerException());
        when(jpaEventRepository.saveAndFlush(any(EventEntity.class))).thenThrow(dataIntegrityException);

        assertThatThrownBy(() -> eventRepositoryAdapter.saveAndCheckConstraints(event))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage( "Foo");
    }

    @Test
    public void shouldThrowExceptionIfIsNotDataIntegrityViolation() {
        final var nullPointerException = new NullPointerException("Bar");
        when(jpaEventRepository.saveAndFlush(any(EventEntity.class))).thenThrow(nullPointerException);

        assertThatThrownBy(() -> eventRepositoryAdapter.saveAndCheckConstraints(event))
                .isInstanceOf(NullPointerException.class)
                .hasMessage( "Bar");
    }

    @RepeatedTest(5)
    public void shouldPaginateAsExpected() {
        final int pageNumber = ThreadLocalRandom.current().nextInt(1, 100);
        final int pageSize = ThreadLocalRandom.current().nextInt(1, 100);

        final var page = new PageImpl<>(IntStream.of(pageSize, pageNumber)
                .mapToObj(this::mockForId)
                .map(EventMapper::toEntity)
                .toList());

        when(jpaEventRepository.findAll(any(Pageable.class))).thenReturn(page);
        eventRepositoryAdapter.list(pageNumber, pageSize);

        verify(jpaEventRepository, times(1)).findAll(pageableCaptor.capture());
        final var pageable = pageableCaptor.getValue();

        assertThat(pageable.getPageNumber()).isEqualTo(pageNumber -1);
        assertThat(pageable.getPageSize()).isEqualTo(pageSize);

        final var sortOrder = pageable.getSort().stream().toList();
        assertThat(sortOrder).hasSize(2);

        assertThat(sortOrder.getFirst().getProperty()).isEqualTo("createdDatetime");
        assertThat(sortOrder.getFirst().getDirection()).isEqualTo(Sort.Direction.DESC);

        assertThat(sortOrder.getLast().getProperty()).isEqualTo("id");
        assertThat(sortOrder.getLast().getDirection()).isEqualTo(Sort.Direction.DESC);
    }
}
