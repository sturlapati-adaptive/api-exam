--liquibase formatted sql
--changeset public:create-instrument-table
CREATE TABLE instrument
(
    id              serial PRIMARY KEY,
    name            text                                                           not null,
    instrument_type text                                                           not null,
    site_id         integer,
    properties      jsonb,
    created         timestamp without time zone default (now() at time zone 'utc') NOT NULL,
    updated         timestamp without time zone default (now() at time zone 'utc') NOT NULL,
    foreign key (site_id) references site (id)
);
--rollback DROP TABLE IF EXISTS instrument CASCADE;
