create sequence if not exists ems_users_id_seq
    start with 1
    increment by 50
    minvalue 1
    no maxvalue
    cache 1;

create table if not exists ems_users
(
    id               bigint primary key,
    name             varchar(200) not null,
    email            varchar(200) not null unique,
    created_datetime timestamp default now()
);

-- drop table ems_users;
-- drop sequence ems_users_id_seq;