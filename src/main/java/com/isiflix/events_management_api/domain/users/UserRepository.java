package com.isiflix.events_management_api.domain.users;

import java.util.Optional;

public interface UserRepository {
    User findOrSave(User user);

    Optional<User> findById(Long userId);
}
