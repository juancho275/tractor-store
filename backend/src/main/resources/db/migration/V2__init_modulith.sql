-- ============================================================
-- V2 - Spring Modulith infrastructure tables
-- Required by Spring Modulith for the Outbox pattern
-- and async domain event publication tracking
-- ============================================================

CREATE TABLE event_publication (
    id                UUID        NOT NULL,
    listener_id       TEXT        NOT NULL,
    event_type        TEXT        NOT NULL,
    serialized_event  TEXT        NOT NULL,
    publication_date  TIMESTAMP WITH TIME ZONE NOT NULL,
    completion_date   TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id)
);

CREATE INDEX idx_event_publication_completion
    ON event_publication (completion_date);

CREATE INDEX idx_event_publication_date
    ON event_publication (publication_date);