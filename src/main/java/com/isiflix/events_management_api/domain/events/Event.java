package com.isiflix.events_management_api.domain.events;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.events.vos.EventPeriodVO;
import com.isiflix.events_management_api.domain.events.vos.PrettyNameVO;

import java.math.BigDecimal;

public class Event {
    private Long id;
    private String name;
    private PrettyNameVO prettyName;
    private String location;
    private BigDecimal price;
    private EventPeriodVO period;

    /**
     * New Instance Constructor
     * <p>
     * It should be used to create a new event instance, when it doesn't exist on the storage yet
     * </p>
     * @param name event name, it will be used to generate the pretty name
     * @param location event location, where the event will be placed
     * @param price event price, it can be for free, but don't try to get smart about it
     * @param period event period, it should be consistence, otherwise it won't instantiate
     */
    protected Event(String name, String location, BigDecimal price, EventPeriodVO period) {
        setName(name);
        setLocation(location);
        setPrice(price);
        setPeriod(period);

        generatePrettyNameFromName();
    }

    /**
     * Deserialization Instance Constructor
     * <p>
     * It should be used to create an existing instance, when it's been validated already
     * </p>
     *
     * @param id event unique identifier
     * @param name event name, stored on the database
     * @param prettyName event prettyName, stored on the database
     * @param location event location, stored on the database
     * @param price event price, stored on the database
     * @param period event period, stored on the database
     */
    protected Event(Long id, String name, PrettyNameVO prettyName, String location, BigDecimal price, EventPeriodVO period) {
        setId(id);
        setName(name);
        setPrettyName(prettyName);
        setLocation(location);
        setPrice(price);
        setPeriod(period);
    }


    /**
     * Serialization DTO Factory
     * <p>
     * The Event entity it's inheriting the responsibility of creating the dto, to avoid exposing internal fields
     * </p>
     * @return event data transfer object, specialized for serialization
     */
    public EventDTO toDTO() {
        return new EventDTO(
                this.id,
                this.name,
                this.prettyName.prettyName(),
                this.location,
                this.price,
                this.period.startDateTime().toLocalDate(),
                this.period.endDateTime().toLocalDate(),
                this.period.startDateTime().toLocalTime(),
                this.period.endDateTime().toLocalTime()
        );
    }

    protected void setId(Long id) {
        if(id == null || id < 0) {
            throw new IllegalArgumentException("Event 'id' cannot be null or negative");
        }

        this.id = id;
    }

    private void setPrettyName(PrettyNameVO prettyName) {
        if(prettyName == null) {
            throw new IllegalArgumentException("Event 'prettyName' cannot be null");
        }

        this.prettyName = prettyName;
    }

    private void generatePrettyNameFromName() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Event 'name' should not be null or blank");
        }

        this.prettyName = PrettyNameVO.of(name);
    }

    private void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Event 'name' should not be null or blank");
        }

        this.name = name;
    }

    private void setLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Event 'location' should not be null or blank");
        }

        this.location = location;
    }

    private void setPrice(BigDecimal price) {
        if (BigDecimal.ZERO.compareTo(price) > 0) {
            throw new IllegalArgumentException("Event 'price' should not be negative");
        }

        this.price = price;
    }

    private void setPeriod(EventPeriodVO period) {
        if (period == null) {
            throw new IllegalArgumentException("Event 'period' be provided");
        }

        this.period = period;
    }

    public Long getId() { return this.id; }
    public PrettyNameVO getPrettyName() { return this.prettyName; }
}
