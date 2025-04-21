package com.isiflix.events_management_api.domain.events;

import com.isiflix.events_management_api.domain.events.vos.EventReferralPodium;
import com.isiflix.events_management_api.domain.events.vos.EventReferralRank;
import com.isiflix.events_management_api.domain.users.User;

public interface SubscriptionRepository {
    Subscription saveAndCheckConstraints(Subscription subscription);

    EventReferralPodium calculatePodiumForEvent(Event event, int podiumSize);

    EventReferralRank calculateRankForUser(Event event, User user);
}
