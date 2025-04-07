package com.isiflix.events_management_api.infra.database.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JPAEventRepository extends JpaRepository<EventEntity, Long> {
    Optional<EventEntity> findByPrettyName(String name);
}
