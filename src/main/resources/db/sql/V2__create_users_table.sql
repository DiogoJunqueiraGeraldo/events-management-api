create table if not exists ems_users
(
    id               bigserial primary key,
    name             varchar(200) not null,
    email            varchar(200) not null unique,
    created_datetime timestamp default now()
);

-- drop table ems_users;