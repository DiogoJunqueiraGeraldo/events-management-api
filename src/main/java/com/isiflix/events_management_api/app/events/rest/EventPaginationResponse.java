package com.isiflix.events_management_api.app.events.rest;

import java.util.List;

public record EventPaginationResponse(
    List<EventResponse> items,
    PaginationSummary pagination
) {
    public record PaginationSummary(int page, int size) {}

    public static EventPaginationResponse from(List<EventResponse> items, int page, int size) {
        return new EventPaginationResponse(items, new PaginationSummary(page, size));
    }
}
