package com.isiflix.events_management_api.app.subscriptions.rest.responses;

public record UserReferralRankResponse(
        Long rankingPosition,
        Long userId,
        String name,
        Long count
) {
}
