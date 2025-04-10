package com.isiflix.events_management_api.domain.errors;

import java.util.Arrays;
import java.util.Optional;

public enum ViolationCode {
    CONFLICT_PRETTY_NAME_ALREADY_EXISTS("conflict-pretty-name-already-exists");

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
