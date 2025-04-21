package com.isiflix.events_management_api.app.subscriptions.rest.responses;

import com.isiflix.events_management_api.app.subscriptions.dtos.SubscriptionDTO;

public record CreateSubscriptionResponse(Long subscriptionId, String designation) {
    public static CreateSubscriptionResponse fromDTO(SubscriptionDTO dto, String host) {
        return new CreateSubscriptionResponse(dto.id(), buildDesignation(dto, host));
    }

    public static String buildDesignation(SubscriptionDTO dto, String host) {
        return host.concat("/subscriptions/%s/%d").formatted(dto.eventDTO().prettyName(), dto.userDTO().id());
    }
}
