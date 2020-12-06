ALTER TABLE images
    ADD COLUMN content_type varchar(128);

UPDATE images
SET content_type = '';

ALTER TABLE images
    ALTER COLUMN content_type SET NOT NULL;
