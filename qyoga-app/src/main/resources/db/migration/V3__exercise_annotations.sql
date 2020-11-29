ALTER TABLE exercises
    ADD COLUMN annotation text;

UPDATE exercises
SET annotation = '';

ALTER TABLE exercises
    ALTER COLUMN annotation SET NOT NULL;
