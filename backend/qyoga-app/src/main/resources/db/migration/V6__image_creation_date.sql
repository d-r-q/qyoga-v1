ALTER TABLE images
    ADD COLUMN created_at timestamp without time zone NOT NULL DEFAULT now();