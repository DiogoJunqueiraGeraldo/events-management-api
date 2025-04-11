package com.isiflix.events_management_api.infra.database.event;

import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventRepository;
import com.isiflix.events_management_api.domain.events.vos.PrettyNameVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JPAEventRepositoryAdapter implements EventRepository {
    private final JPAEventRepository jpaEventRepository;

    @Autowired
    public JPAEventRepositoryAdapter(
            JPAEventRepository jpaEventRepository
    ) {
        this.jpaEventRepository = jpaEventRepository;
    }

    @Override
    public Event save(Event event) {
        final var entity = EventMapper.toEntity(event);
        this.jpaEventRepository.save(entity);
        return EventMapper.fromEntity(entity);
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
}
