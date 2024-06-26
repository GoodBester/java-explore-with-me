DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS compilations_events CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name   varchar(250) NOT NULL,
    email  varchar(254) UNIQUE NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS categories (
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name   varchar(100) UNIQUE NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS compilations (
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned  BOOLEAN,
    title   varchar(200) NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS locations (
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat  float NOT NULL,
    lon  float NOT NULL,
    CONSTRAINT pk_location PRIMARY KEY(id)
);


CREATE TABLE IF NOT EXISTS events (
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation          varchar(2000) NOT NULL,
    category_id         BIGINT REFERENCES categories(id) ON DELETE CASCADE NOT NULL,
    description         varchar(7000) NOT NULL,
    confirmed_requests  BIGINT,
    created_on          TIMESTAMP WITHOUT TIME ZONE,
    event_date          TIMESTAMP WITHOUT TIME ZONE,
    initiator_id        BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
    location_id         BIGINT REFERENCES locations(id) ON DELETE CASCADE,
    paid                BOOLEAN,
    participant_limit   INTEGER,
    request_moderation  BOOLEAN,
    title               varchar(120) NOT NULL,
    views               BIGINT,
    published_on        TIMESTAMP WITHOUT TIME ZONE,
    state               varchar(100),
    CONSTRAINT pk_event PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS compilations_events (
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_id        BIGINT REFERENCES events(id),
    compilation_id  BIGINT REFERENCES compilations(id)
);

CREATE TABLE IF NOT EXISTS requests (
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id        BIGINT REFERENCES events(id) ON DELETE CASCADE NOT NULL,
    requester_id    BIGINT REFERENCES users(id) ON DELETE CASCADE  NOT NULL,
    created         TIMESTAMP WITHOUT TIME ZONE,
    status          varchar(100),
    CONSTRAINT pk_requests PRIMARY KEY(id)
);

