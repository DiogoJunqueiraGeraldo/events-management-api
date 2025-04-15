package com.isiflix.events_management_api.infra.database.subscriptions;

import com.isiflix.events_management_api.domain.events.Subscription;
import com.isiflix.events_management_api.domain.events.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JPASubscriptionRepositoryAdapter implements SubscriptionRepository {
    private final JPASubscriptionRepository jpaSubscriptionRepository;

    @Autowired
    public JPASubscriptionRepositoryAdapter(JPASubscriptionRepository jpaSubscriptionRepository) {
        this.jpaSubscriptionRepository = jpaSubscriptionRepository;
    }

    @Override
    public Subscription saveAndCheckConstraints(Subscription subscription) {
        SubscriptionEntity entity = SubscriptionMapper.toEntity(subscription);
        jpaSubscriptionRepository.saveAndFlush(entity);
        return SubscriptionMapper.fromEntity(entity);
    }
}
