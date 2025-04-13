package com.isiflix.events_management_api.infra.database.events;

import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventFactory;

public class EventMapper {
    public static EventEntity toEntity(Event event) {
        final var dto = event.toDTO();
        return new EventEntity(
                dto.id(),
                dto.name(),
                dto.prettyName(),
                dto.location(),
                dto.price(),
                dto.startDateTime(),
                dto.endDateTime()
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
