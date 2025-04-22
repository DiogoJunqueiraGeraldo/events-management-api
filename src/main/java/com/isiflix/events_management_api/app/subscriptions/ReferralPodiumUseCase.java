package com.isiflix.events_management_api.app.subscriptions;

import com.isiflix.events_management_api.app.events.FindEventUseCase;
import com.isiflix.events_management_api.app.subscriptions.dtos.ReferralPodiumItemDTO;
import com.isiflix.events_management_api.domain.events.EventFactory;
import com.isiflix.events_management_api.domain.events.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReferralPodiumUseCase {
    private static final int PODIUM_SIZE = 3;
    private final SubscriptionRepository subscriptionRepository;
    private final FindEventUseCase findEventUseCase;

    @Autowired
    public ReferralPodiumUseCase(SubscriptionRepository subscriptionRepository, FindEventUseCase findEventUseCase) {
        this.subscriptionRepository = subscriptionRepository;
        this.findEventUseCase = findEventUseCase;
    }

    public List<ReferralPodiumItemDTO> getPodiumRanking(String prettyName) {
        final var eventDTO = findEventUseCase.findEventByPrettyNameOrThrow(prettyName);
        final var event = EventFactory.of(eventDTO);

        return subscriptionRepository.calculatePodiumForEvent(event, PODIUM_SIZE)
                .podium()
                .entrySet()
                .stream()
                .map(entry -> new ReferralPodiumItemDTO(entry.getKey().getName(), entry.getValue()))
                .sorted(Comparator.comparingLong(ReferralPodiumItemDTO::subscribers).reversed())
                .collect(Collectors.toList());
    }
}
