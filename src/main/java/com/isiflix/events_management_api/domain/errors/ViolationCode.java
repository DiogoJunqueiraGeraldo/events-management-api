package com.isiflix.events_management_api.domain.errors;

import java.util.Arrays;
import java.util.Optional;

public enum ViolationCode {
    CONFLICT_PRETTY_NAME_ALREADY_EXISTS("conflict-pretty-name-already-exists"),
    CONFLICT_USER_ALREADY_SUBSCRIBED_TO_EVENT("conflict-user-already-subscribed-to-event"),
    CONFLICT_CANT_REFER_ITSELF("conflict-can-refer-itself"),
    EVENT_NOT_FOUND_BY_PRETTY_NAME("event-not-found-by-pretty-name"),
    USER_NOT_FOUND_BY_ID("user-not-found-by-id"),;

    private final String code;
    ViolationCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

    public static Optional<ViolationCode> of(String code) {
        return Arrays.stream(ViolationCode.values())
                .filter(o -> o.code.equals(code))
                .findFirst();
    }
}
