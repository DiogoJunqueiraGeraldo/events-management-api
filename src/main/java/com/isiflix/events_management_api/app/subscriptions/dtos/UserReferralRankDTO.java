package com.isiflix.events_management_api.app.subscriptions.dtos;

import com.isiflix.events_management_api.app.users.dtos.UserDTO;

public record UserReferralRankDTO(
        Long rankingPosition,
        Long referralsCounter,
        UserDTO userDTO
) {
}
