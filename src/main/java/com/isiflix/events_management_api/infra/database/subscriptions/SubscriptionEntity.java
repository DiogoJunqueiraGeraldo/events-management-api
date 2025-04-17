package com.isiflix.events_management_api.infra.database.subscriptions;

import com.isiflix.events_management_api.infra.database.events.EventEntity;
import com.isiflix.events_management_api.infra.database.users.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ems_subscriptions")
public class SubscriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscription_seq")
    @SequenceGenerator(
            name = "subscription_seq",
            sequenceName = "ems_subscriptions_id_seq",
            allocationSize = 50
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "referrer")
    private UserEntity referrer;

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime = LocalDateTime.now();

    public SubscriptionEntity(Long id, EventEntity event, UserEntity user, UserEntity referrer) {
        this.id = id;
        this.event = event;
        this.user = user;
        this.referrer = referrer;
    }
}
