package com.isiflix.events_management_api.domain.events.vos;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EventPrettyNameTest {
    @Test
    public void shouldConstructValidEventPrettyName() {
        final var eventPrettyName = new EventPrettyName("foo-bar-baz");
        assertThat(eventPrettyName.toString()).isEqualTo("foo-bar-baz");
        assertThat(eventPrettyName.prettyName()).isEqualTo("foo-bar-baz");
    }

    @Test
    public void shouldGenerateExpectedPrettyNames() {
        var expectedPrettyNames = Map.of(
                "already-formated-pretty-name", "already-formated-pretty-name",
                "Make Capital Letters Lower Case", "make-capital-letters-lower-case",
                "Also remove           spaces", "also-remove-spaces",
                "Should handle special characters!!!", "should-handle-special-characters",
                "And 1, 2, or three numbers", "and-1-2-or-three-numbers",
                "I forgot to say about the commas \uD83D\uDE05", "i-forgot-to-say-about-the-commas",
                "And the emojis!\uD83D\uDE05", "and-the-emojis"
        );

        assertThat(expectedPrettyNames.entrySet()).allSatisfy((entry) -> {
            var eventPrettyName = EventPrettyName.of(entry.getKey());
            assertThat(eventPrettyName.prettyName()).isEqualTo(entry.getValue());
        });
    }

    @Test
    public void shouldRequireNonNullPrettyNameWhenUsingFactoryMethod() {
        var invalidPrettyNames = new String[]{null, "", " "};

        Consumer<String> throwForInvalidPrettyName = (invalidPrettyName) -> assertThatThrownBy(() -> EventPrettyName.of(invalidPrettyName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event 'prettyName' cannot be null or blank");

        assertThat(invalidPrettyNames).allSatisfy(throwForInvalidPrettyName);
    }

    @Test
    public void shouldRequireNonNullPrettyNameWhenUsingConstructor() {
        var invalidPrettyNames = new String[]{null, "", " "};

        Consumer<String> throwForInvalidPrettyName = (invalidPrettyName) -> assertThatThrownBy(() -> new EventPrettyName(invalidPrettyName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event 'prettyName' cannot be null or blank");

        assertThat(invalidPrettyNames).allSatisfy(throwForInvalidPrettyName);
    }

    @Test
    public void shouldRequireNormalizedPrettyNameWhenUsingConstructor() {
        assertThatThrownBy(() -> new EventPrettyName("Not Normalized"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event 'prettyName' provided doesn't fit pretty name normative");
    }
}
