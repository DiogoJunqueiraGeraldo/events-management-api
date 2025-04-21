package com.isiflix.events_management_api.domain.users;

import com.isiflix.events_management_api.app.users.dtos.CreateUserDTO;
import com.isiflix.events_management_api.app.users.dtos.UserDTO;

public class UserFactory {
    public static User of(CreateUserDTO createUserDTO) {
        return new User(null, createUserDTO.name(), createUserDTO.email());
    }

    public static User of(UserDTO dto) {
        return new User(dto.id(), dto.name(), dto.email());
    }

    public static User fromRaw(Long id, String name, String email) {
        return new User(id, name, email);
    }
}
