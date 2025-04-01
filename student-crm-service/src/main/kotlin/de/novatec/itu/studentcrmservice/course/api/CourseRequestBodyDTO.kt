package de.novatec.itu.studentcrmservice.course.api

import jakarta.validation.constraints.Pattern

const val COURSE_NAME_REGEX = "^[a-zA-Z0-9_-]{2,25}( [a-zA-Z0-9]{2,25})*$"

data class CourseRequestBodyDTO(
    @field:Pattern(
        regexp = COURSE_NAME_REGEX,
        message = "Course name should not be blank or it does not match pattern $COURSE_NAME_REGEX"
    )
    val courseName: String
)
