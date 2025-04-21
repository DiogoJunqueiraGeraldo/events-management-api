package com.isiflix.events_management_api.domain.events;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.events.vos.EventPeriod;
import com.isiflix.events_management_api.domain.events.vos.EventPrettyName;
import com.isiflix.events_management_api.domain.users.User;

import java.math.BigDecimal;

public class Event {
    private Long id;
    private String name;
    private EventPrettyName eventPrettyName;
    private String location;
    private BigDecimal price;
    private EventPeriod period;

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
    protected Event(String name, String location, BigDecimal price, EventPeriod period) {
        setName(name);
        setLocation(location);
        setPrice(price);
        setPeriod(period);

        this.eventPrettyName = EventPrettyName.of(name);
    }

    /**
     * Deserialization Instance Constructor
     * <p>
     * It should be used to create an existing instance, when it's been validated already
     * </p>
     *
     * @param id event unique identifier
     * @param name event name, stored on the database
     * @param eventPrettyName event prettyName, stored on the database
     * @param location event location, stored on the database
     * @param price event price, stored on the database
     * @param period event period, stored on the database
     */
    protected Event(Long id, String name, EventPrettyName eventPrettyName, String location, BigDecimal price, EventPeriod period) {
        setId(id);
        setName(name);
        setPrettyName(eventPrettyName);
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
                this.eventPrettyName.toString(),
                this.location,
                this.price,
                this.period.startDateTime(),
                this.period.endDateTime()
        );
    }

    public Subscription subscribe(User user, User referrer) {
        return new Subscription(null, this, user, referrer);
    }

    public void setId(Long id) {
        if(id == null || id < 0) {
            throw new IllegalArgumentException("Event 'id' should not be null or negative");
        }

        if(this.id != null) {
            throw new IllegalArgumentException("Event 'id' has already been set");
        }

        this.id = id;
    }

    private void setPrettyName(EventPrettyName eventPrettyName) {
        if(eventPrettyName == null) {
            throw new IllegalArgumentException("Event 'prettyName' should not be null");
        }

        this.eventPrettyName = eventPrettyName;
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
        if (price == null || BigDecimal.ZERO.compareTo(price) > 0) {
            throw new IllegalArgumentException("Event 'price' should not be negative or null");
        }

        this.price = price;
    }

    private void setPeriod(EventPeriod period) {
        if (period == null) {
            throw new IllegalArgumentException("Event 'period' should not be null");
        }

        this.period = period;
    }

    public Long getId() { return this.id; }
    public EventPrettyName getPrettyName() { return this.eventPrettyName; }
}
