package com.isiflix.events_management_api.domain.events.vos;

import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.users.User;

public record EventReferralRank(
        Event event,
        User user,
        Long position,
        Long referralsCounter) {
}
