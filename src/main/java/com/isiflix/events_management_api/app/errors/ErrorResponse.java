package com.isiflix.events_management_api.app.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record ErrorResponse(
        String code,
        String message,
        OffsetDateTime moment,
        @JsonInclude(Include.NON_NULL) Map<String, List<String>> issues
) {
    ErrorResponse(String code, String message, Map<String, List<String>> issues) {
        this(code, message, OffsetDateTime.now(), issues);
    }
}
