package com.isiflix.events_management_api.infra.database.subscriptions;

import com.isiflix.events_management_api.infra.database.events.EventEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ems_subscriptions")
public class SubscriptionEntity {
    @Embeddable
    @EqualsAndHashCode
    public static class SubscriptionEntityKey implements Serializable {
        private Long eventId;
        private Long userId;
    }

    @EmbeddedId
    private SubscriptionEntityKey id;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime = LocalDateTime.now();
}
