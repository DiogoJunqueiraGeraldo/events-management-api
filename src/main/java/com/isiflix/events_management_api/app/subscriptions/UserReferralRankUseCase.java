package com.isiflix.events_management_api.app.subscriptions;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.app.users.dtos.UserDTO;
import com.isiflix.events_management_api.app.subscriptions.dtos.UserReferralRankDTO;
import com.isiflix.events_management_api.domain.events.EventFactory;
import com.isiflix.events_management_api.domain.events.SubscriptionRepository;
import com.isiflix.events_management_api.domain.users.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserReferralRankUseCase {
    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public UserReferralRankUseCase(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public UserReferralRankDTO getUserReferralRank(EventDTO eventDTO, UserDTO userDTO) {
        final var event = EventFactory.of(eventDTO);
        final var user = UserFactory.of(userDTO);
        final var rankVo = subscriptionRepository.calculateRankForUser(event, user);
        return new UserReferralRankDTO(rankVo.position(), rankVo.referralsCounter(), userDTO);
    }
}
