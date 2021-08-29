--liquibase formatted sql
--changeset public:create-site-table
CREATE TABLE site
(
    id          serial PRIMARY KEY,
    name     text not null,
    shipping_address jsonb,
    created     timestamp without time zone default (now() at time zone 'utc') NOT NULL,
    updated     timestamp without time zone default (now() at time zone 'utc') NOT NULL
);
--rollback DROP TABLE IF EXISTS site CASCADE;
