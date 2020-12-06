ALTER TABLE exercises_images
    ADD COLUMN index int;

ALTER TABLE exercises_images
    DROP CONSTRAINT exercises_images_pkey;

ALTER TABLE exercises_images
    ADD PRIMARY KEY (exercise_id, image_id, index);

ALTER TABLE exercises_images
    ADD CONSTRAINT image_id_key UNIQUE (image_id);