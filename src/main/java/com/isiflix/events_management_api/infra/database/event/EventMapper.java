package com.isiflix.events_management_api.infra.database.event;

import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventFactory;

public class EventMapper {
    public static EventEntity toEntity(Event event) {
        final var dto = event.toDTO();
        return new EventEntity(
                dto.name(),
                dto.prettyName(),
                dto.location(),
                dto.price(),
                dto.startDate().atTime(dto.startTime()),
                dto.endDate().atTime(dto.endTime())
        );
    }

    public static Event fromEntity(EventEntity eventEntity) {
        return EventFactory.fromRaw(
                eventEntity.getId(),
                eventEntity.getName(),
                eventEntity.getPrettyName(),
                eventEntity.getLocation(),
                eventEntity.getPrice(),
                eventEntity.getStartDatetime(),
                eventEntity.getEndDatetime()
        );
    }
}
