create sequence ems_subscriptions_id_seq
    start with 1
    increment by 50
    minvalue 1
    no maxvalue
    cache 1;

create table if not exists ems_subscriptions
(
    id               bigint primary key,
    event_id         bigint not null,
    user_id          bigint not null,
    created_datetime timestamp default now(),
    unique (event_id, user_id),
    foreign key (event_id) references ems_events (id) on delete cascade,
    foreign key (user_id) references ems_users (id) on delete cascade
);


-- drop table ems_subscriptions;
-- drop sequence ems_subscriptions_id_seq;