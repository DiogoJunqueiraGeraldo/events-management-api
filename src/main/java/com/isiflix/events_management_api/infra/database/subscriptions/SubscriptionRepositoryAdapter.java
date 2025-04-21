package com.isiflix.events_management_api.infra.database.subscriptions;

import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.Subscription;
import com.isiflix.events_management_api.domain.events.SubscriptionRepository;
import com.isiflix.events_management_api.domain.events.vos.EventReferralPodium;
import com.isiflix.events_management_api.domain.events.vos.EventReferralRank;
import com.isiflix.events_management_api.domain.users.User;
import com.isiflix.events_management_api.infra.database.users.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class SubscriptionRepositoryAdapter implements SubscriptionRepository {
    private final JPASubscriptionRepository jpaSubscriptionRepository;

    @Autowired
    public SubscriptionRepositoryAdapter(JPASubscriptionRepository jpaSubscriptionRepository) {
        this.jpaSubscriptionRepository = jpaSubscriptionRepository;
    }

    @Override
    public Subscription saveAndCheckConstraints(Subscription subscription) {
        SubscriptionEntity entity = SubscriptionMapper.toEntity(subscription);
        jpaSubscriptionRepository.saveAndFlush(entity);
        return SubscriptionMapper.fromEntity(entity);
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
