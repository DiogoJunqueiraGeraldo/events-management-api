package com.isiflix.events_management_api.infra.database.users;

import com.isiflix.events_management_api.domain.users.User;
import com.isiflix.events_management_api.domain.users.UserFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryAdapterTest {
    @Mock
    JPAUserRepository jpaUserRepository;

    @Mock
    ConstraintViolationException constraintViolationException;
    
    @InjectMocks
    UserRepositoryAdapter userRepositoryAdapter;

    static User user;

    @BeforeAll
    static void setUp() {
        user = UserFactory.fromRaw(1L, "John Doe", "john@doe.com");
    }

    @Test
    public void shouldFlushForCheckConstraintsOnSave() {
        when(jpaUserRepository.saveAndFlush(any(UserEntity.class))).thenReturn(UserMapper.toEntity(user));
        userRepositoryAdapter.saveIfNotExists(user);
        verify(jpaUserRepository, times(1)).saveAndFlush(any(UserEntity.class));
    }

    @Test
    public void shouldOnConflictDoNothingIfConstraintViolationException() {
        final var ex = new DataIntegrityViolationException("Foo", constraintViolationException);

        when(jpaUserRepository.saveAndFlush(any(UserEntity.class))).thenThrow(ex);
        when(jpaUserRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(UserMapper.toEntity(user)));

        final var foundUser = userRepositoryAdapter.saveIfNotExists(user);

        verify(jpaUserRepository, times(1)).saveAndFlush(any(UserEntity.class));
        verify(jpaUserRepository, times(1)).findByEmail(user.getEmail());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
        assertThat(foundUser.getName()).isEqualTo(user.getName());
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void shouldOnConflictThrowIfNotConstraintViolationException() {
        final var ex = new DataIntegrityViolationException("Foo", new NullPointerException());

        when(jpaUserRepository.saveAndFlush(any(UserEntity.class))).thenThrow(ex);

        assertThatThrownBy(() -> userRepositoryAdapter.saveIfNotExists(user))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("Foo");
    }

    @Test
    public void shouldOnConflictDoNothingButIfNotFoundThrows() {
        final var ex = new DataIntegrityViolationException("Foo", constraintViolationException);

        when(jpaUserRepository.saveAndFlush(any(UserEntity.class))).thenThrow(ex);
        when(jpaUserRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userRepositoryAdapter.saveIfNotExists(user))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Can't find inserted user entity");
    }
}
