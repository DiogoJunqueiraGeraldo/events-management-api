package com.isiflix.events_management_api.app.events;

import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.domain.events.EventRepository;
import com.isiflix.events_management_api.domain.events.vos.EventPrettyName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FindEventUseCaseTest {
    @Mock
    EventRepository eventRepository;

    @InjectMocks
    FindEventUseCase findEventUseCase;

    @Test
    public void shouldThrowBusinessExceptionWhenNotFound() {
        when(eventRepository.findByPrettyName(any(EventPrettyName.class))).thenReturn(Optional.empty());
        final var exception = catchThrowable(() -> findEventUseCase.findEventByPrettyNameOrThrow("any"));

        assertThat(exception)
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("Event not found by 'prettyName'");

        final var businessRuleViolationException = (BusinessRuleViolationException) exception;
        assertThat(businessRuleViolationException.getViolationCode()).isEqualTo(ViolationCode.EVENT_NOT_FOUND_BY_PRETTY_NAME);
        assertThat(businessRuleViolationException.getMetadata()).containsEntry("prettyName", "any");
    }
}