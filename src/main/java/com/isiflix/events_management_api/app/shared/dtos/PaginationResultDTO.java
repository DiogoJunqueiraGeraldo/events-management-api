package com.isiflix.events_management_api.app.shared.dtos;


import java.util.List;

public record PaginationResultDTO<T>(
    List<T> items,
    PaginationSummary pagination
) {
    public record PaginationSummary(int page, int size) {}

    public static <T> PaginationResultDTO<T> from(List<T> items, int page, int size) {
        return new PaginationResultDTO<>(items, new PaginationSummary(page, size));
    }
}
