package com.isiflix.events_management_api.app.subscriptions;

import com.isiflix.events_management_api.app.subscriptions.dtos.CreateSubscriptionDTO;
import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.domain.events.EventRepository;
import com.isiflix.events_management_api.domain.events.vos.PrettyNameVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CreateSubscriptionUseCase {
    private final EventRepository eventRepository;

    @Autowired
    public CreateSubscriptionUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void createNewSubscription(CreateSubscriptionDTO createSubscriptionDTO) {
        final var event = eventRepository.findByPrettyName(PrettyNameVO.of(createSubscriptionDTO.prettyName()));
        if (event.isEmpty()) {
            throw new BusinessRuleViolationException(
                    ViolationCode.CANT_SUBSCRIBE_TO_NON_EXISTING_EVENT,
                    "Event with pretty name %s not found".formatted(createSubscriptionDTO.prettyName()),
                    Map.of("prettyName", createSubscriptionDTO.prettyName())
            );
        }
    }
}
