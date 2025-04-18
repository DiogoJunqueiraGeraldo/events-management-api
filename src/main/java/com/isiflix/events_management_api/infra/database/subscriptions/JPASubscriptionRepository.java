package com.isiflix.events_management_api.infra.database.subscriptions;

import com.isiflix.events_management_api.infra.database.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public interface JPASubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    @Query(
            value = """
                SELECT users.id, users.name, users.email, COUNT(*) as total
                    FROM ems_subscriptions
                LEFT JOIN ems_users users ON users.id = referrer
                    WHERE referrer IS NOT NULL AND event_id = :eventId
                GROUP BY users.id, users.name, users.email
                ORDER BY total DESC
                LIMIT :limit
            """,
            nativeQuery = true
    )
    List<Object[]> countReferralsForEventQuery(Long eventId, int limit);

    default Map<UserEntity, Long> countReferralsForEvent(Long eventId, int limit) {
        return this.countReferralsForEventQuery(eventId, limit)
                .stream()
                .collect(Collectors.toMap(
                        row -> new UserEntity((Long) row[0], (String) row[1], (String) row[2]),
                        row -> (Long) row[3]
                ));
    }

    @Query(
            value = """
                SELECT *
                    FROM (
                        SELECT
                            users.id,
                            COUNT(*) AS total,
                            RANK() OVER (ORDER BY COUNT(*) DESC) AS position
                        FROM ems_subscriptions
                        LEFT JOIN ems_users users ON users.id = referrer
                        WHERE referrer IS NOT NULL AND event_id = :eventId
                        GROUP BY users.id, users.name, users.email
                    ) ranked
                WHERE id = :userId
                LIMIT 1
            """,
            nativeQuery = true
    )
    List<Object[]> rankPositionForEventAndUserQuery(Long eventId, Long userId);

    record RankPositionDTO(Long referralCounter, Long rankPosition) {}

    default RankPositionDTO rankPositionForEventAndUser(Long eventId, Long userId) {
        List<Object[]> rows = this.rankPositionForEventAndUserQuery(eventId, userId);
        Object[] row = rows.getFirst();
        return new RankPositionDTO((Long) row[1], (Long) row[2]);
    }
}
