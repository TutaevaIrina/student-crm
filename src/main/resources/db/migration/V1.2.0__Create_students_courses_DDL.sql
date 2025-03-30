CREATE TABLE IF NOT EXISTS students_courses (
    students_id BIGINT NOT NULL,
    courses_id BIGINT NOT NULL,
    PRIMARY KEY (students_id, courses_id),
    CONSTRAINT fk_students FOREIGN KEY (students_id) REFERENCES students (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_courses FOREIGN KEY (courses_id) REFERENCES courses (id) ON UPDATE CASCADE ON DELETE CASCADE
);



