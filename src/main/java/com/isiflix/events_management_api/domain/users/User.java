package com.isiflix.events_management_api.domain.users;

import com.isiflix.events_management_api.app.subscriptions.dtos.UserDTO;

public class User {
    private final Long id;
    private final String name;
    private final String email;

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;

        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("User 'name' cannot be null or empty");
        }

        if(email == null || email.isEmpty()) {
            throw new IllegalArgumentException("User 'email' cannot be null or empty");
        }
    }

    public UserDTO toDTO() {
        return new UserDTO(id, name, email);
    }

    public String getEmail() {
        return email;
    }

    public Object getId() {
        return id;
    }
}
