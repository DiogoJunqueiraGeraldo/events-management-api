package com.isiflix.events_management_api.infra.database.subscriptions;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JPASubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
}
