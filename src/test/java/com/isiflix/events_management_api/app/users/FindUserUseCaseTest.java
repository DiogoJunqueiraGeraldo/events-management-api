package com.isiflix.events_management_api.app.users;

import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.domain.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FindUserUseCaseTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    FindUserUseCase findUserUseCase;

    @Test
    public void shouldThrowBusinessExceptionWhenNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        final var exception = catchThrowable(() -> findUserUseCase.findUserByIDOrThrow(2L));

        assertThat(exception)
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("User not found by 'id'");

        final var businessRuleViolationException = (BusinessRuleViolationException) exception;
        assertThat(businessRuleViolationException.getViolationCode()).isEqualTo(ViolationCode.USER_NOT_FOUND_BY_ID);
        assertThat(businessRuleViolationException.getMetadata()).containsEntry("id", 2L);
    }
}