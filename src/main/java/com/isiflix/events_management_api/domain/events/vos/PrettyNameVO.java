package com.isiflix.events_management_api.domain.events.vos;

public record PrettyNameVO(String prettyName) {
    public PrettyNameVO(String prettyName) {
        this.prettyName = prettyName;
        validateRequirements();
        validateCohesion();
    }

    public static PrettyNameVO of(String name) {
        return new PrettyNameVO(normalize(name));
    }

    private static String normalize(String name) {
        return name.replaceAll(" ", "-")
                .replaceAll("[^a-zA-Z0-9 -]", "")
                .toLowerCase();
    }

    private void validateRequirements() {
        if (prettyName == null || prettyName.isBlank()) {
            throw new IllegalArgumentException("Event 'prettyName' cannot be null or blank");
        }
    }

    private void validateCohesion() {
        if (prettyName == null || prettyName.isBlank() || !normalize(prettyName).equals(prettyName)) {
            throw new IllegalArgumentException("Event 'prettyName' provided doesn't fit pretty name normative");
        }
    }
}
