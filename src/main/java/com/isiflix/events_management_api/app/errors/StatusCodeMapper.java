package com.isiflix.events_management_api.app.errors;

import com.isiflix.events_management_api.domain.errors.ViolationCode;
import org.springframework.http.HttpStatus;

public class StatusCodeMapper {
    /**
     * Maps a {@link ViolationCode} to its corresponding HTTP status code.
     * <p>
     * This method uses a {@code switch} expression intentionally without a {@code default} branch.
     * This design enforces exhaustive handling of all known {@link ViolationCode} values at compile time.
     * <p>
     * The goal is to **fail fast** during development whenever a new violation code is introduced,
     * ensuring developers make a conscious decision about the appropriate HTTP status for each case.
     * <p>
     * TL;DR: You added a new {@code ViolationCode}? You better decide its HTTP status, or this won't compile.
     *
     * @param violationCode the domain-level violation code
     * @return the HTTP status code to be returned by the application layer
     */
    public int of(ViolationCode violationCode) {
        final var statusCode = switch (violationCode) {
            case CONFLICT_PRETTY_NAME_ALREADY_EXISTS, CONFLICT_CANT_REFER_ITSELF,
                 CONFLICT_USER_ALREADY_SUBSCRIBED_TO_EVENT -> HttpStatus.CONFLICT;
            case CANT_SUBSCRIBE_TO_NON_EXISTING_EVENT, EVENT_NOT_FOUND_BY_PRETTY_NAME,
                 USER_NOT_FOUND_BY_ID -> HttpStatus.NOT_FOUND;
            // don't you dare add a default clause
        };

        return statusCode.value();
    }
}
