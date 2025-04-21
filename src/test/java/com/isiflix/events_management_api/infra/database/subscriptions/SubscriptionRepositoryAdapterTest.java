package com.isiflix.events_management_api.infra.database.subscriptions;

import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventFactory;
import com.isiflix.events_management_api.domain.events.Subscription;
import com.isiflix.events_management_api.domain.events.vos.EventPeriodTest;
import com.isiflix.events_management_api.domain.users.User;
import com.isiflix.events_management_api.domain.users.UserFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionRepositoryAdapterTest {
    @Mock
    JPASubscriptionRepository jpaSubscriptionRepository;

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
}
