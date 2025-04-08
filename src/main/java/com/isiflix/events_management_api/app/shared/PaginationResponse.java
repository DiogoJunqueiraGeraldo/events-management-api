package com.isiflix.events_management_api.app.shared;


import java.util.List;

public record PaginationResponse<T>(
    List<T> items,
    PaginationSummary pagination
) {
    public record PaginationSummary(int page, int size) {}

    public static <T> PaginationResponse<T> from(List<T> items, int page, int size) {
        return new PaginationResponse<>(items, new PaginationSummary(page, size));
    }
}
