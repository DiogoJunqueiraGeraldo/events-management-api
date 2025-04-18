package com.isiflix.events_management_api.app.users;

import com.isiflix.events_management_api.app.users.dtos.UserDTO;
import com.isiflix.events_management_api.domain.users.User;
import com.isiflix.events_management_api.domain.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FindUserUseCase {
    private final UserRepository userRepository;

    @Autowired
    public FindUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserDTO> findUserById(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail()));
    }

    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::toDTO);
    }
}
