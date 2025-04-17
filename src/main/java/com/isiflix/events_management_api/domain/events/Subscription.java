package com.isiflix.events_management_api.domain.events;

import com.isiflix.events_management_api.app.subscriptions.dtos.SubscriptionDTO;
import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.domain.users.User;

import java.util.Map;

public record Subscription(Long id, Event event, User user, User referrer) {
    public Subscription(Long id, Event event, User user, User referrer) {
        this.id = id;
        this.event = event;
        this.user = user;
        this.referrer = referrer;

        if(event == null) {
            throw new IllegalArgumentException("Subscription 'event' cannot be null");
        }

        if(user == null) {
            throw new IllegalArgumentException("Subscription 'user' cannot be null");
        }

        if(user.equals(referrer)) {
            throw new BusinessRuleViolationException(
                    ViolationCode.CONFLICT_CANT_REFER_ITSELF,
                    "Subscription 'referrer' cannot be the same user",
                    Map.of("userId", user.getId())
            );
        }
    }

    public SubscriptionDTO toDTO() {
        return new SubscriptionDTO(id, event.toDTO(), user.toDTO(), buildDesignation());
    }

    private String buildDesignation() {
        return "https://devstage.com/%s/%d".formatted(
                event.getPrettyName().prettyName(),
                user.getId()
        );
    }
}
