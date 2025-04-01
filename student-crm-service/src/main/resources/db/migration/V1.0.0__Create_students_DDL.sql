CREATE TABLE IF NOT EXISTS students (
    id BIGINT NOT NULL,
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT pk_students_id PRIMARY KEY(id),
    CONSTRAINT udx_students_id UNIQUE(email)
);

CREATE SEQUENCE IF NOT EXISTS seq_students_id;
ALTER SEQUENCE IF EXISTS seq_students_id OWNED BY students.id;