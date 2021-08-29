--liquibase formatted sql
--changeset public:create-container-table
CREATE TABLE container
(
    id          serial PRIMARY KEY,
    barcode     uuid unique,
    description text,
    instrument_id integer,
    created     timestamp without time zone default (now() at time zone 'utc') NOT NULL,
    updated     timestamp without time zone default (now() at time zone 'utc') NOT NULL,
    foreign key (instrument_id) references instrument(id)
);
--rollback DROP TABLE IF EXISTS container CASCADE;
