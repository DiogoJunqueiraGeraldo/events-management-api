package com.isiflix.events_management_api.infra.database.event;

import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventRepository;
import com.isiflix.events_management_api.domain.events.vos.PrettyNameVO;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JPAEventRepositoryAdapter implements EventRepository {
    private final EntityManager entityManager;
    private final JPAEventRepository jpaEventRepository;

    @Autowired
    public JPAEventRepositoryAdapter(
            JPAEventRepository jpaEventRepository,
            EntityManager entityManager
    ) {
        this.jpaEventRepository = jpaEventRepository;
        this.entityManager = entityManager;
    }

    @Override
    public void save(Event event) {
        final var entity = EventMapper.toEntity(event);
        this.jpaEventRepository.save(entity);
    }

    @Override
    public List<Event> list(int page, int size) {
        Pageable pageable = PageRequest.of(page -1, size, Sort.by("createdDatetime").descending());
        return jpaEventRepository.findAll(pageable)
                .map(EventMapper::fromEntity)
                .toList();
    }

    @Override
    public Optional<Event> findByPrettyName(PrettyNameVO prettyName) {
        return jpaEventRepository.findByPrettyName(prettyName.prettyName())
                .map(EventMapper::fromEntity);
    }

    /**
     * Retrieves the next value from the PostgreSQL sequence associated with the
     * 'ems_events' table. This approach guarantees ID generation directly from
     * the database, ensuring consistency and collision-free behavior across
     * distributed application instances.
     *
     * <p>
     * This native query leverages PostgreSQL's built-in sequence mechanism,
     * which is inherently thread-safe and avoids ID collisions without relying
     * on in-memory counters or UUIDs. Suitable for horizontally scaled systems.
     * </p>
     *
     * <p>
     * Important: Although `nextval(...)` does not create row-level locks,
     * it does serialize access to the sequence object itself. This means
     * that in extremely high-throughput environments, it can become a
     * contention point. If your application is expected to generate thousands
     * of IDs per second across multiple nodes, consider using a hi-lo strategy
     * or segment allocation to reduce sequence pressure, or switching to a more
     * distributed ID generation approach, such as Snowflake, ULID, or UUIDv7.
     * </p>
     */
    public Long nextId() {
        return (Long) entityManager
                .createNativeQuery("SELECT nextval('ems_events_id_seq')")
                .getSingleResult();
    }
}
