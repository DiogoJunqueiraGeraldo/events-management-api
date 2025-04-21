package com.isiflix.events_management_api.app.users;

import com.isiflix.events_management_api.app.users.dtos.CreateUserDTO;
import com.isiflix.events_management_api.app.users.dtos.UserDTO;
import com.isiflix.events_management_api.domain.users.UserFactory;
import com.isiflix.events_management_api.domain.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateUserUseCase {
    private final UserRepository userRepository;

    @Autowired
    public CreateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO createUser(CreateUserDTO dto) {
        final var user = UserFactory.create(dto);
        return userRepository.saveIfNotExists(user).toDTO();
    }
}
