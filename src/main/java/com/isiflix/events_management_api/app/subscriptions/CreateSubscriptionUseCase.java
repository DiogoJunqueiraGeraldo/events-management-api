package com.isiflix.events_management_api.app.subscriptions;

import com.isiflix.events_management_api.app.subscriptions.dtos.CreateSubscriptionDTO;
import com.isiflix.events_management_api.app.subscriptions.dtos.SubscriptionDTO;
import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventRepository;
import com.isiflix.events_management_api.domain.events.Subscription;
import com.isiflix.events_management_api.domain.events.vos.PrettyNameVO;
import com.isiflix.events_management_api.domain.events.SubscriptionRepository;
import com.isiflix.events_management_api.domain.users.User;
import com.isiflix.events_management_api.domain.users.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class CreateSubscriptionUseCase {
    private final static String SUBSCRIPTION_UNIQUE_CONSTRAINT = "ems_subscriptions_event_id_user_id_key";
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public CreateSubscriptionUseCase(
            EventRepository eventRepository,
            UserRepository userRepository,
            SubscriptionRepository subscriptionRepository
    ) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public SubscriptionDTO createNewSubscription(CreateSubscriptionDTO dto) {
        final var event = findEvent(dto.prettyName());
        final var user = findUserOrCreate(dto.email(), dto.userName());

        try {
            final var subscription = createNewSubscription(event, user);
            return subscription.toDTO();
        } catch (DataIntegrityViolationException e) {
            if (isSubscriptionConstraintViolation(e)) {
                throwSubscriptionAlreadyExists(event, user);
            }

            throw e;
        }
    }

    private void throwSubscriptionAlreadyExists(Event event, User user) {
        throw new BusinessRuleViolationException(
                ViolationCode.CONFLICT_USER_ALREADY_SUBSCRIBED_TO_EVENT,
                "Can't subscribe twice to same event",
                Map.of(
                        "event", event.getPrettyName().prettyName(),
                        "user", user.getEmail()
                )
        );
    }

    private boolean isSubscriptionConstraintViolation(DataIntegrityViolationException dataIntegrityViolationException) {
        return dataIntegrityViolationException.getCause() instanceof ConstraintViolationException constraintViolationException
                && Objects.equals(constraintViolationException.getConstraintName(), SUBSCRIPTION_UNIQUE_CONSTRAINT);
    }

    private Subscription createNewSubscription(Event event, User user) {
        final var subscription = event.subscribe(user);
        return subscriptionRepository.saveAndCheckConstraints(subscription);
    }

    private Event findEvent(String prettyName) {
        final var prettyNameVo = PrettyNameVO.of(prettyName);
        return eventRepository
                .findByPrettyName(prettyNameVo)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        ViolationCode.CANT_SUBSCRIBE_TO_NON_EXISTING_EVENT,
                        "Event with pretty name %s not found".formatted(prettyName),
                        Map.of("prettyName", prettyName)
                ));
    }

    private User findUserOrCreate(String email, String userName) {
        final var user = new User(null, userName, email);
        return userRepository.findOrSave(user);
    }
}
