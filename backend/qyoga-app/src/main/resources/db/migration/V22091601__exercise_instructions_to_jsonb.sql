ALTER TABLE exercises
    DROP COLUMN instructions;

ALTER TABLE exercises
    ADD COLUMN
        instructions JSONB NOT NULL DEFAULT '[]';

DROP TABLE exercises_images;