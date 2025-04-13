package com.isiflix.events_management_api.app.subscriptions.rest;

import jakarta.validation.constraints.NotBlank;

public record CreateSubscriptionRequest(
        @NotBlank(message = "The 'userName' field is required and cannot be empty or blank.")
        String userName,

        @NotBlank(message = "The 'email' field is required and cannot be empty or blank.")
        String email
) {
}
