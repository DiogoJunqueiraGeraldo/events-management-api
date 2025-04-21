package com.isiflix.events_management_api.infra.database.users;

import com.isiflix.events_management_api.app.users.dtos.UserDTO;
import com.isiflix.events_management_api.domain.users.User;
import com.isiflix.events_management_api.domain.users.UserFactory;

public class UserMapper {
    public static UserEntity toEntity(User user) {
        UserDTO dto = user.toDTO();
        return new UserEntity(dto.id(), dto.name(), dto.email());
    }

    public static User fromEntity(UserEntity entity) {
        return UserFactory.fromRaw(entity.getId(), entity.getName(), entity.getEmail());
    }
}
