package com.isiflix.events_management_api.infra.database.users;

import com.isiflix.events_management_api.domain.users.User;
import com.isiflix.events_management_api.domain.users.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryAdapter.class);
    private final JPAUserRepository jpaUserRepository;

    @Autowired
    public UserRepositoryAdapter(JPAUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User findOrSave(User user) {
        return jpaUserRepository.findByEmail(user.getEmail())
            .map(UserMapper::fromEntity)
            .orElseGet(() -> this.saveOnConflictDoNothing(user));
    }

    @Override
    public Optional<User> findById(Long userId) {
        return jpaUserRepository.findById(userId)
                .map(UserMapper::fromEntity);
    }

    private User saveOnConflictDoNothing(User user) {
        UserEntity entity = UserMapper.toEntity(user);

        try {
            final var persistedEntity = jpaUserRepository.saveAndFlush(entity);
            return UserMapper.fromEntity(persistedEntity);
        } catch (DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException) {
                logger.trace("User {} was not inserted into the database due to conflict", user.getEmail());

                return jpaUserRepository.findByEmail(user.getEmail())
                        .map(UserMapper::fromEntity)
                        .orElseThrow(() -> new IllegalStateException("Can't find inserted user entity"));
            }

            throw e;
        }
    }
}
