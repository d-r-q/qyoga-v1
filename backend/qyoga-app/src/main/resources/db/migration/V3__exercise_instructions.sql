ALTER TABLE exercises
    ADD COLUMN instructions text;

UPDATE exercises
SET instructions = '';

ALTER TABLE exercises
    ALTER COLUMN instructions SET NOT NULL;
