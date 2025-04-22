package com.isiflix.events_management_api.app.subscriptions.dtos;

public record CreateSubscriptionDTO(String prettyName, String userName, String email, Long referrerId) { }
