package com.isiflix.events_management_api.app.subscriptions;

import com.isiflix.events_management_api.app.events.FindEventUseCase;
import com.isiflix.events_management_api.app.users.FindUserUseCase;
import com.isiflix.events_management_api.app.subscriptions.dtos.UserReferralRankDTO;
import com.isiflix.events_management_api.domain.events.EventFactory;
import com.isiflix.events_management_api.domain.events.SubscriptionRepository;
import com.isiflix.events_management_api.domain.users.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserReferralRankUseCase {
    private final SubscriptionRepository subscriptionRepository;
    private final FindEventUseCase findEventUseCase;
    private final FindUserUseCase findUserUseCase;

    @Autowired
    public UserReferralRankUseCase(SubscriptionRepository subscriptionRepository,
                                   FindEventUseCase findEventUseCase,
                                   FindUserUseCase findUserUseCase
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.findEventUseCase = findEventUseCase;
        this.findUserUseCase = findUserUseCase;
    }

    public UserReferralRankDTO getUserReferralRank(String prettyName, Long userId) {
        final var eventDTO = findEventUseCase.findEventByPrettyNameOrThrow(prettyName);
        final var userDTO = findUserUseCase.findUserByIDOrThrow(userId);

        final var rankVo = subscriptionRepository.calculateRankForUser(
                EventFactory.of(eventDTO),
                UserFactory.of(userDTO)
        );

        return new UserReferralRankDTO(rankVo.position(), rankVo.referralsCounter(), userDTO);
    }
}
