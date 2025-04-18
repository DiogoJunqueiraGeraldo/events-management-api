package com.isiflix.events_management_api.domain.events.vos;

import com.isiflix.events_management_api.domain.users.User;

import java.util.Map;

public record ReferralPodiumVO(Map<User, Long> podium) {
}
