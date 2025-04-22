package com.isiflix.events_management_api.infra.database.subscriptions;

import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.Subscription;
import com.isiflix.events_management_api.domain.events.SubscriptionRepository;
import com.isiflix.events_management_api.domain.events.vos.EventReferralPodium;
import com.isiflix.events_management_api.domain.events.vos.EventReferralRank;
import com.isiflix.events_management_api.domain.users.User;
import com.isiflix.events_management_api.infra.database.users.UserMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class SubscriptionRepositoryAdapter implements SubscriptionRepository {
    public final static String SUBSCRIPTION_UNIQUE_CONSTRAINT = "ems_subscriptions_event_id_user_id_key";
    private final JPASubscriptionRepository jpaSubscriptionRepository;

    @Autowired
    public SubscriptionRepositoryAdapter(JPASubscriptionRepository jpaSubscriptionRepository) {
        this.jpaSubscriptionRepository = jpaSubscriptionRepository;
    }

    @Override
    public Subscription saveAndCheckConstraints(Subscription subscription) {
        SubscriptionEntity entity = SubscriptionMapper.toEntity(subscription);

        try {
            jpaSubscriptionRepository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            if (isSubscriptionConstraintViolation(e)) {
                throwSubscriptionAlreadyExists(subscription.event(), subscription.user());
            }

            throw e;
        }

        return SubscriptionMapper.fromEntity(entity);
    }

    private void throwSubscriptionAlreadyExists(Event event, User user) {
        throw new BusinessRuleViolationException(
                ViolationCode.CONFLICT_USER_ALREADY_SUBSCRIBED_TO_EVENT,
                "Can't subscribe twice to same event",
                Map.of(
                        "event", event.getPrettyName(),
                        "user", user.getEmail()
                )
        );
    }

    private boolean isSubscriptionConstraintViolation(DataIntegrityViolationException dataIntegrityViolationException) {
        return dataIntegrityViolationException.getCause() instanceof ConstraintViolationException constraintViolationException
                && Objects.equals(constraintViolationException.getConstraintName(), SUBSCRIPTION_UNIQUE_CONSTRAINT);
    }

    @Override
    public EventReferralPodium calculatePodiumForEvent(Event event, int podiumSize) {
        return new EventReferralPodium(jpaSubscriptionRepository
                .countReferralsForEvent(
                        event.getId(),
                        podiumSize
                )
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(entry -> UserMapper.fromEntity(entry.getKey()),
                                Map.Entry::getValue)
                ));
    }

    @Override
    public EventReferralRank calculateRankForUser(Event event, User user) {
        final var rank = jpaSubscriptionRepository.rankPositionForEventAndUser(event.getId(), user.getId());
        return new EventReferralRank(event, user, rank.rankPosition(), rank.referralCounter());
    }
}
