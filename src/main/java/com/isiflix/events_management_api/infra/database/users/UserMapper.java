package com.isiflix.events_management_api.infra.database.users;

import com.isiflix.events_management_api.app.subscriptions.dtos.UserDTO;
import com.isiflix.events_management_api.domain.users.User;

public class UserMapper {
    public static UserEntity toEntity(User user) {
        UserDTO dto = user.toDTO();
        return new UserEntity(dto.id(), dto.name(), dto.email());
    }

    public static User fromEntity(UserEntity entity) {
        return new User(entity.getId(), entity.getName(), entity.getEmail());
    }
}
