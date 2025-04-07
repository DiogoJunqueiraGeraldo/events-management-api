create table if not exists ems_subscriptions
(
    event_id         bigint not null,
    user_id          bigint not null,
    created_datetime timestamp default now(),
    primary key (event_id, user_id),
    foreign key (event_id) references ems_events (id) on delete cascade,
    foreign key (user_id) references ems_users (id) on delete cascade
);


-- drop table ems_subscriptions