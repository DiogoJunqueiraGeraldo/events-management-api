create table if not exists ems_events
(
    id               bigserial primary key,
    name             varchar(200)   not null,
    pretty_name      varchar(200)   not null unique,
    location         varchar(200)   not null default 'online',
    price            decimal(11, 2) not null check (price >= 0),
    start_datetime   timestamp      not null,
    end_datetime     timestamp      not null,
    created_datetime timestamp               default now(),
    check (end_datetime > start_datetime)
);

-- drop table ems_events;