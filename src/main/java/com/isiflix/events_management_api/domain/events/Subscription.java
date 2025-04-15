package com.isiflix.events_management_api.domain.events;

import com.isiflix.events_management_api.app.subscriptions.dtos.SubscriptionDTO;
import com.isiflix.events_management_api.domain.users.User;

public record Subscription(Long id, Event event, User user) {
    public Subscription(Long id, Event event, User user) {
        this.id = id;
        this.event = event;
        this.user = user;

        if(event == null) {
            throw new IllegalArgumentException("Subscription 'event' cannot be null");
        }

        if(user == null) {
            throw new IllegalArgumentException("Subscription 'user' cannot be null");
        }
    }

    public SubscriptionDTO toDTO() {
        return new SubscriptionDTO(id, event.toDTO(), user.toDTO());
    }
}
