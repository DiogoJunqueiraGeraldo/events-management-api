package com.isiflix.events_management_api.domain.users;

import com.isiflix.events_management_api.app.users.dtos.UserDTO;

public class User {
    private final Long id;
    private final String name;
    private final String email;

    protected User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;

        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("User 'name' cannot be null or empty");
        }

        if(email == null || email.isBlank()) {
            throw new IllegalArgumentException("User 'email' cannot be null or empty");
        }
    }

    public UserDTO toDTO() {
        return new UserDTO(id, name, email);
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        if (!id.equals(user.id)) return false;
        return email.equals(user.email);
    }
}
