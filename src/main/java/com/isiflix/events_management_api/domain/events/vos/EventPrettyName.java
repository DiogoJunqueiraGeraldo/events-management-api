package com.isiflix.events_management_api.domain.events.vos;

public record EventPrettyName(String prettyName) {
    public EventPrettyName(String prettyName) {
        this.prettyName = prettyName;
        validateRequirements();
        validateCohesion();
    }

    public static EventPrettyName of(String name) {
        return new EventPrettyName(normalize(name));
    }

    private static String normalize(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Event 'prettyName' cannot be null or blank");
        }

        return name
                .replaceAll("\\s+", "-")                     // remove consecutive spaces
                .replaceAll("[^a-zA-Z0-9\\-]", "")           // remove invalid characters
                .replaceAll("^-+|-+$", "")                   // remove starting and ending slashes
                .toLowerCase();
    }

    private void validateRequirements() {
        if (prettyName == null || prettyName.isBlank()) {
            throw new IllegalArgumentException("Event 'prettyName' cannot be null or blank");
        }
    }

    private void validateCohesion() {
        if (!normalize(prettyName).equals(prettyName)) {
            throw new IllegalArgumentException("Event 'prettyName' provided doesn't fit pretty name normative");
        }
    }

    @Override
    public String toString() {
        return prettyName;
    }
}
