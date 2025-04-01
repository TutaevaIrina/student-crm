CREATE TABLE IF NOT EXISTS courses (
    id BIGINT NOT NULL,
    course_name VARCHAR(40) NOT NULL,
    CONSTRAINT pk_courses_id PRIMARY KEY(id)
);

CREATE SEQUENCE IF NOT EXISTS seq_courses_id;
ALTER SEQUENCE IF EXISTS seq_courses_id OWNED BY courses.id;