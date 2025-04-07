package com.isiflix.events_management_api.infra.database.event;

import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ems_events")
public class EventEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "pretty_name", nullable = false, length = 200, unique = true)
    private String prettyName;

    @Column(nullable = false, length = 200)
    private String location = "online";

    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal price;

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDatetime;

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime = LocalDateTime.now();

    public EventEntity(
            String name,
            String prettyName,
            String location,
            BigDecimal price,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime
    ) {
        this.name = name;
        this.prettyName = prettyName;
        this.location = location;
        this.price = price;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
    }
}

