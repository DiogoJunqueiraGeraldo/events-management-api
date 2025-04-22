package com.isiflix.events_management_api.app.subscriptions;

import com.isiflix.events_management_api.app.events.FindEventUseCase;
import com.isiflix.events_management_api.app.subscriptions.dtos.CreateSubscriptionDTO;
import com.isiflix.events_management_api.app.users.CreateUserUseCase;
import com.isiflix.events_management_api.app.users.FindUserUseCase;
import com.isiflix.events_management_api.app.users.dtos.CreateUserDTO;
import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventFactory;
import com.isiflix.events_management_api.domain.events.Subscription;
import com.isiflix.events_management_api.domain.events.SubscriptionRepository;
import com.isiflix.events_management_api.domain.events.vos.EventPeriodTest;
import com.isiflix.events_management_api.domain.users.User;
import com.isiflix.events_management_api.domain.users.UserFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateSubscriptionUseCaseTest {
    @Mock
    FindEventUseCase findEventUseCase;
    @Mock
    FindUserUseCase findUserUseCase;
    @Mock
    CreateUserUseCase createUserUseCase;
    @Mock
    SubscriptionRepository subscriptionRepository;

    @InjectMocks
    CreateSubscriptionUseCase createSubscriptionUseCase;

    @Captor
    private ArgumentCaptor<CreateUserDTO> createUserDTOArgumentCaptor;

    static User referrer;
    static Event event;
    static User user;
    static Subscription subscriptionWithReferral;
    static Subscription subscriptionWithoutReferral;

    @BeforeAll
    public static void setUp() {
        final var referenceDateTime = LocalDateTime.now();
        referrer = UserFactory.fromRaw(1L, "Barry Allen", "barry@allen.com");
        user = UserFactory.fromRaw(2L, "John Doe", "john@doe.com");
        event = EventFactory.fromRaw(
                1L, "Foo Bar", "foo-bar",
                "online", BigDecimal.ZERO, referenceDateTime,
                referenceDateTime.plusMinutes(EventPeriodTest.VALID_EVENT_DURATION_IN_MINUTES));

        subscriptionWithReferral = event.subscribe(user, referrer);
        subscriptionWithoutReferral = event.subscribe(user, null);
    }

    @Test
    public void shouldCreateSubscriptionWithReferrer() {
        when(findUserUseCase.findUserById(1L)).thenReturn(Optional.of(referrer.toDTO()));
        when(findEventUseCase.findEventByPrettyNameOrThrow("foo-bar")).thenReturn(event.toDTO());
        when(findUserUseCase.findByEmail("john@doe.com")).thenReturn(Optional.of(user.toDTO()));
        when(subscriptionRepository.saveAndCheckConstraints(any())).thenReturn(subscriptionWithReferral);

        final var createSubscriptionDTO = new CreateSubscriptionDTO(
                "foo-bar",
                "John Doe",
                "john@doe.com",
                1L
        );

        final var subscriptionDTO = createSubscriptionUseCase.createNewSubscription(createSubscriptionDTO);

        assertThat(subscriptionDTO).isNotNull();
        assertThat(subscriptionDTO.userDTO()).isEqualTo(user.toDTO());
        assertThat(subscriptionDTO.eventDTO()).isEqualTo(event.toDTO());
        assertThat(subscriptionDTO.referrerDTO()).isEqualTo(referrer.toDTO());
    }

    @Test
    public void shouldCreateSubscriptionWithoutReferrer() {
        when(findEventUseCase.findEventByPrettyNameOrThrow("foo-bar")).thenReturn(event.toDTO());
        when(findUserUseCase.findByEmail("john@doe.com")).thenReturn(Optional.of(referrer.toDTO()));
        when(subscriptionRepository.saveAndCheckConstraints(any())).thenReturn(subscriptionWithoutReferral);

        final var createSubscriptionDTO = new CreateSubscriptionDTO(
                "foo-bar",
                "John Doe",
                "john@doe.com",
                null
        );

        final var subscriptionDTO = createSubscriptionUseCase.createNewSubscription(createSubscriptionDTO);

        assertThat(subscriptionDTO).isNotNull();
        assertThat(subscriptionDTO.userDTO()).isEqualTo(user.toDTO());
        assertThat(subscriptionDTO.eventDTO()).isEqualTo(event.toDTO());
        assertThat(subscriptionDTO.referrerDTO()).isNull();
    }

    @Test
    public void shouldNotHanldeEventNoFoundException() {
        final var expectedException = new BusinessRuleViolationException(
                ViolationCode.EVENT_NOT_FOUND_BY_PRETTY_NAME,
                "Event not found by 'prettyName'",
                Map.of("prettyName", "foo-bar")
        );

        when(findEventUseCase.findEventByPrettyNameOrThrow("foo-bar")).thenThrow(expectedException);

        final var createSubscriptionDTO = new CreateSubscriptionDTO(
                "foo-bar",
                "John Doe",
                "john@doe.com",
                null
        );

        final var ex = catchThrowable(() -> createSubscriptionUseCase.createNewSubscription(createSubscriptionDTO));
        assertThat(ex).isEqualTo(expectedException);
    }

    @Test
    public void shouldCreateUserWhenNotExists() {
        when(findEventUseCase.findEventByPrettyNameOrThrow("foo-bar")).thenReturn(event.toDTO());
        when(findUserUseCase.findByEmail("john@doe.com")).thenReturn(Optional.empty());
        when(createUserUseCase.createUser(any())).thenReturn(user.toDTO());
        when(subscriptionRepository.saveAndCheckConstraints(any())).thenReturn(subscriptionWithoutReferral);

        final var createSubscriptionDTO = new CreateSubscriptionDTO(
                "foo-bar",
                "John Doe",
                "john@doe.com",
                null
        );

        final var subscriptionDTO = createSubscriptionUseCase.createNewSubscription(createSubscriptionDTO);
        assertThat(subscriptionDTO).isNotNull();
        assertThat(subscriptionDTO.userDTO()).isEqualTo(user.toDTO());
        assertThat(subscriptionDTO.eventDTO()).isEqualTo(event.toDTO());
        assertThat(subscriptionDTO.referrerDTO()).isNull();

        verify(createUserUseCase, times(1))
                .createUser(createUserDTOArgumentCaptor.capture());

        final var createUserDTO = createUserDTOArgumentCaptor.getValue();
        assertThat(createUserDTO).isNotNull();
        assertThat(createUserDTO.name()).isEqualTo("John Doe");
        assertThat(createUserDTO.email()).isEqualTo("john@doe.com");
    }
}