package com.isiflix.events_management_api.domain.events;

public interface SubscriptionRepository {
    Subscription saveAndCheckConstraints(Subscription subscription);
}
