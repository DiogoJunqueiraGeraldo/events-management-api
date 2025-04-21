package com.isiflix.events_management_api.domain.users;

import com.isiflix.events_management_api.app.users.dtos.CreateUserDTO;
import com.isiflix.events_management_api.app.users.dtos.UserDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserFactoryTest {
    @Test
    public void shouldCreateNewUser() {
        final var user = UserFactory.create(new CreateUserDTO("Foo Bar", "foo@bar.com"));
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isEqualTo("Foo Bar");
        assertThat(user.getEmail()).isEqualTo("foo@bar.com");
    }

    @Test
    public void shouldDeserializeUser() {
        final var user = UserFactory.fromRaw(1L, "Foo Bar", "foo@bar.com");
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Foo Bar");
        assertThat(user.getEmail()).isEqualTo("foo@bar.com");
    }

    @Test
    public void shouldReconstructUser() {
        final var user = UserFactory.of(new UserDTO(1L, "Foo Bar", "foo@bar.com"));
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Foo Bar");
        assertThat(user.getEmail()).isEqualTo("foo@bar.com");
    }
}
