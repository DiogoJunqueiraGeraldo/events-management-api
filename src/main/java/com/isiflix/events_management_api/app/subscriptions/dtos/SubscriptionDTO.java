package com.isiflix.events_management_api.app.subscriptions.dtos;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;

public record SubscriptionDTO(Long id, EventDTO eventDTO, UserDTO userDTO, String designation) {
    public SubscriptionDTO(Long id, EventDTO eventDTO, UserDTO userDTO) {
        this(id, eventDTO, userDTO, buildDesignationURL(eventDTO, userDTO));
    }

    public static String buildDesignationURL(EventDTO eventDTO, UserDTO userDTO) {
        return "https://devstage.com/%s/%d".formatted(
                eventDTO.prettyName(),
                userDTO.id()
        );
    }
}
