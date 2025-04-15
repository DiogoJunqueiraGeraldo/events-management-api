package com.isiflix.events_management_api.app.subscriptions.dtos;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;

public record SubscriptionDTO(Long id, EventDTO eventDTO, UserDTO userDTO, String designation) {
}
