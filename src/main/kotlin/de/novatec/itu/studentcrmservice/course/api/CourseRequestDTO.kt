package de.novatec.itu.studentcrmservice.course.api

import de.novatec.itu.studentcrmservice.course.persistence.CourseEntity
import jakarta.validation.constraints.Pattern

class CourseRequestDTO(
    @field:Pattern(
        regexp = "^([a-zA-Z0-9_-]{2,25})( [a-zA-Z0-9]{2,25})*$",
        message = "Course name should not be blank or it does not match pattern"
    )
    val courseName: String
) {
    fun toEntity(): CourseEntity {
        return CourseEntity(
                id = 0,
            courseName = this.courseName
        )
    }
}
