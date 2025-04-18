package com.isiflix.events_management_api.domain.users;

import java.util.Optional;

public interface UserRepository {
    User saveIfNotExists(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long userId);
}
