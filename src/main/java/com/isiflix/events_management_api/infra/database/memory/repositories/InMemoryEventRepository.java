package com.isiflix.events_management_api.infra.database.memory.repositories;

import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventRepository;
import com.isiflix.events_management_api.domain.events.vos.PrettyNameVO;
import com.isiflix.events_management_api.infra.database.memory.models.EventRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryEventRepository implements EventRepository {
    private static final AtomicLong eventIdSequence = new AtomicLong(0);
    private static final ConcurrentHashMap<Long, EventRecord> storage = new ConcurrentHashMap<>();

    private Long generateIdIfAbsent(Event event) {
        return (event.getId() != null) ? event.getId() : eventIdSequence.getAndAdd(1);
    }

    @Override
    public Long save(Event event) {
        final var id = generateIdIfAbsent(event);
        final var record = EventRecord.of(id, event.toDTO());
        storage.put(id, record);
        return id;
    }

    @Override
    public List<Event> list(int page, int size) {
        long skip = (page - 1l) * size;

        return storage.values()
                .stream()
                .skip(skip)
                .limit(size)
                .map(EventRecord::toEntity)
                .toList();
    }

    /// I don't like linear search either, but I won't build a lookup rn
    /// Please be patient and wait the real database impl
    @Override
    public Optional<Event> findByPrettyName(PrettyNameVO prettyName) {
        return storage
                .values()
                .stream()
                .filter(er -> er.prettyName().equals(prettyName.prettyName()))
                .findFirst()
                .map(EventRecord::toEntity);
    }
}