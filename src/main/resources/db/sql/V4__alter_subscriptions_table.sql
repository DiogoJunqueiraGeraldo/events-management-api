alter table ems_subscriptions
    add column referrer bigint,
    add constraint fk_referrer
        foreign key (referrer) references ems_users (id) on delete set null;

-- alter table ems_subscriptions drop column referrer;