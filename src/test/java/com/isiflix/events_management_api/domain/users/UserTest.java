package com.isiflix.events_management_api.domain.users;

import com.isiflix.events_management_api.app.users.dtos.UserDTO;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserTest {
    @Test
    public void shouldBeEqualToItself() {
        final var user = UserFactory.fromRaw(1L, "Foo Bar", "foo@bar.com");
        assertThat(user.equals(user)).isTrue();
    }

    @Test
    public void shouldNotBeEqualToNull() {
        final var user = UserFactory.fromRaw(1L, "Foo Bar", "foo@bar.com");
        assertThat(user.equals(null)).isFalse();
    }

    @Test
    public void shouldNotBeEqualToAnotherClass() {
        final var user = UserFactory.fromRaw(1L, "Foo Bar", "foo@bar.com");
        Object o = new Object();
        assertThat(user.equals(o)).isFalse();
    }

    @Test
    public void shouldNotBeEqualToAnotherObjectWithDifferentId() {
        final var user = UserFactory.fromRaw(1L, "Foo Bar", "foo@bar.com");
        final var other = UserFactory.fromRaw(2L, "Foo Bar", "foo@bar.com");
        assertThat(user.equals(other)).isFalse();
    }

    @Test
    public void shouldNotBeEqualWhenEmailsDiffer() {
        final var user = UserFactory.fromRaw(1L, "Foo Bar", "foo@bar.com");
        final var other = UserFactory.fromRaw(1L, "Foo Bar", "bar@foo.com");

        // In this domain, email uniquely identifies the user — different email, different user.
        assertThat(user.equals(other)).isFalse();
    }

    @Test
    public void shouldBeEqualToAnotherWithSameIdAndEmail() {
        final var user = UserFactory.fromRaw(1L, "Foo Bar", "foo@bar.com");
        final var other = UserFactory.fromRaw(1L, "Foo Bar", "foo@bar.com");

        assertThat(user.equals(other)).isTrue();
    }

    @Test
    public void shouldGenerateDTO() {
        final var dto = new UserDTO(1L, "Foo Bar", "foo@bar.com");
        final var user = UserFactory.of(dto);
        assertThat(user.toDTO()).isEqualTo(dto);
    }

    @Test
    public void shouldRequireName() {
        String[] invalidNames = {null, "", " "};
        Consumer<String> throwForInvalidName = (invalidName) -> assertThatThrownBy(() -> UserFactory.fromRaw(1L, invalidName, "foo@bar.com"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User 'name' cannot be null or empty");

        assertThat(invalidNames).allSatisfy(throwForInvalidName);
    }

    @Test
    public void shouldRequireEmail() {
        String[] invalidEmails = {null, "", " "};
        Consumer<String> throwForInvalidEmail = (invalidEmail) -> assertThatThrownBy(() -> UserFactory.fromRaw(1L, "Foo Bar", invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User 'email' cannot be null or empty");

        assertThat(invalidEmails).allSatisfy(throwForInvalidEmail);
    }
}
