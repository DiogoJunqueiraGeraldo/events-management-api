package com.isiflix.events_management_api.domain.users;

public interface UserRepository {
    User findOrSave(User user);
}
