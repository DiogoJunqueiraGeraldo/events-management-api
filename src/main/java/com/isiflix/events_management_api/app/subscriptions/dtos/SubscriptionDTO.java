package com.isiflix.events_management_api.app.subscriptions.dtos;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.app.users.dtos.UserDTO;

public record SubscriptionDTO(Long id, EventDTO eventDTO, UserDTO userDTO, UserDTO referrerDTO) {
    public SubscriptionDTO(Long id, EventDTO eventDTO, UserDTO userDTO) {
        this(id, eventDTO, userDTO, null);
    }
}
