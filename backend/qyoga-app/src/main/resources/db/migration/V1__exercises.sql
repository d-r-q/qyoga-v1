CREATE TABLE exercises
(
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name        VARCHAR(1024) NOT NULL UNIQUE,
    description TEXT          NOT NULL,
    duration    INT           NOT NULL,
    tags        JSONB         NOT NULL
);

CREATE TABLE tags
(
    id   BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(128) NOT NULL UNIQUE
);

CREATE TABLE exercises_tags
(
    exercise_id BIGINT NOT NULL REFERENCES exercises,
    tag_id      BIGINT NOT NULL REFERENCES tags,
    PRIMARY KEY (exercise_id, tag_id)
);

CREATE TABLE images
(
    id      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name    VARCHAR(128) NOT NULL,
    content BYTEA
);

CREATE TABLE exercises_images
(
    exercise_id BIGINT NOT NULL REFERENCES exercises,
    image_id    BIGINT NOT NULL REFERENCES images,
    PRIMARY KEY (exercise_id, image_id)
);
