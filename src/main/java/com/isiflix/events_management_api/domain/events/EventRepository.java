package com.isiflix.events_management_api.domain.events;

import com.isiflix.events_management_api.domain.events.vos.PrettyNameVO;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Event saveAndCheckConstraints(Event event);

    List<Event> list(int page, int size);

    Optional<Event> findByPrettyName(PrettyNameVO prettyName);
}
