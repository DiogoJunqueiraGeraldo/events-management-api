package com.isiflix.events_management_api.domain.events;

public interface EventRepository {
    Long save(Event event);
}
