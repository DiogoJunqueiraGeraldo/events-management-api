package com.isiflix.events_management_api.domain.events;

import com.isiflix.events_management_api.domain.events.vos.ReferralPodiumVO;
import com.isiflix.events_management_api.domain.events.vos.ReferralRankVO;
import com.isiflix.events_management_api.domain.users.User;

public interface SubscriptionRepository {
    Subscription saveAndCheckConstraints(Subscription subscription);

    ReferralPodiumVO calculatePodiumForEvent(Event event, int podiumSize);

    ReferralRankVO calculateRankForUser(Event event, User user);
}
