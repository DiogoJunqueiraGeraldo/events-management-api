package com.isiflix.events_management_api.infra.database.subscriptions;

import com.isiflix.events_management_api.domain.events.Subscription;
import com.isiflix.events_management_api.infra.database.events.EventMapper;
import com.isiflix.events_management_api.infra.database.users.UserMapper;

public class SubscriptionMapper {
    public static Subscription fromEntity(SubscriptionEntity entity) {
        return new Subscription(
                entity.getId(),
                EventMapper.fromEntity(entity.getEvent()),
                UserMapper.fromEntity(entity.getUser())
        );
    }

    public static SubscriptionEntity toEntity(Subscription subscription) {
        return new SubscriptionEntity(
                subscription.id(),
                EventMapper.toEntity(subscription.event()),
                UserMapper.toEntity(subscription.user())
        );
    }
}
