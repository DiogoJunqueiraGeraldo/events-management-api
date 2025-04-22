package com.isiflix.events_management_api.infra.database.subscriptions;

import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventFactory;
import com.isiflix.events_management_api.domain.events.Subscription;
import com.isiflix.events_management_api.domain.events.vos.EventPeriodTest;
import com.isiflix.events_management_api.domain.users.User;
import com.isiflix.events_management_api.domain.users.UserFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionRepositoryAdapterTest {
    @Mock
    JPASubscriptionRepository jpaSubscriptionRepository;

    @Mock
    ConstraintViolationException constraintViolationException;

    @InjectMocks
    SubscriptionRepositoryAdapter subscriptionRepositoryAdapter;

    static User user;
    static Event event;
    static Subscription subscription;

    @BeforeAll
    public static void setUp() {
        user = UserFactory.fromRaw(1L, "John Doe", "john@doe.com");
        event = EventFactory.fromRaw(1L, "Foo Bar", "foo-bar", "online",
                BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now().plusMinutes(EventPeriodTest.VALID_EVENT_DURATION_IN_MINUTES));
        subscription = event.subscribe(user, null);
    }

    @Test
    public void shouldFlushForCheckConstraintsOnSave() {
        when(jpaSubscriptionRepository.saveAndFlush(any(SubscriptionEntity.class))).thenReturn(SubscriptionMapper.toEntity(subscription));
        subscriptionRepositoryAdapter.saveAndCheckConstraints(subscription);
        verify(jpaSubscriptionRepository, times(1)).saveAndFlush(any(SubscriptionEntity.class));
    }

    @Test
    public void shouldThrowBusinessExceptionIfConstraintViolationUserAlreadySubscribed() {
        when(constraintViolationException.getConstraintName()).thenReturn(SubscriptionRepositoryAdapter.SUBSCRIPTION_UNIQUE_CONSTRAINT);
        final var dataIntegrityException = new DataIntegrityViolationException("Foo", constraintViolationException);

        when(jpaSubscriptionRepository.saveAndFlush(any(SubscriptionEntity.class))).thenThrow(dataIntegrityException);
        final var exception = catchThrowable(() -> subscriptionRepositoryAdapter.saveAndCheckConstraints(subscription));

        assertThat(exception)
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Can't subscribe twice to same event");
    }

    @Test
    public void shouldThrowExceptionIfNotConstraintViolationIsNotTheUniqueSubscriptionConstraint() {
        when(constraintViolationException.getConstraintName()).thenReturn("another");
        final var dataIntegrityException = new DataIntegrityViolationException("Foo", constraintViolationException);

        when(jpaSubscriptionRepository.saveAndFlush(any(SubscriptionEntity.class))).thenThrow(dataIntegrityException);
        final var exception = catchThrowable(() -> subscriptionRepositoryAdapter.saveAndCheckConstraints(subscription));

        assertThat(exception)
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Foo");
    }

    @Test
    public void shouldThrowExceptionIfNotCausedByConstraintViolation() {
        final var dataIntegrityException = new DataIntegrityViolationException("Foo", new NullPointerException());

        when(jpaSubscriptionRepository.saveAndFlush(any(SubscriptionEntity.class))).thenThrow(dataIntegrityException);
        final var exception = catchThrowable(() -> subscriptionRepositoryAdapter.saveAndCheckConstraints(subscription));

        assertThat(exception)
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Foo");
    }
}
