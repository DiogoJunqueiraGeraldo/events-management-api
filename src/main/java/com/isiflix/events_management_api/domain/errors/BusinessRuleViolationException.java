package com.isiflix.events_management_api.domain.errors;

import java.util.Map;

/**
 * Exception thrown when a business rule is violated.
 * <p>
 * Carries a {@link ViolationCode} indicating the violated rule and a metadata map
 * with optional contextual information to assist in debugging.
 * </p>
 *
 * <strong>WARNING:</strong> Do not include any sensitive data in the {@code metadata} map.
 * This includes PII (e.g., email or legal documents like SSN), authentication tokens, card numbers, etc.
 * Metadata may be logged or exposed in monitoring tools.
 * Example of safe usage:
 * <pre>
 * throw new BusinessRuleViolationException(
 *     ViolationCode.EVENT_ALREADY_EXISTS,
 *     "Event already exists",
 *     Map.of("eventId", event.getId())
 * );
 * </pre>
 */
public class BusinessRuleViolationException extends RuntimeException {
    private final ViolationCode violationCode;
    private final Map<String, Object> metadata;

    public BusinessRuleViolationException(ViolationCode violationCode, String message, Map<String, Object> metadata) {
        super(message);
        this.violationCode = violationCode;
        this.metadata = metadata;
    }

    public ViolationCode getViolationCode() {
        return violationCode;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}

