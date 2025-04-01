package com.novatec.studentcrmservice.course.business.model

import com.novatec.studentcrmservice.course.persistence.CourseEntity
import com.novatec.studentcrmservice.shared.types.CourseName

data class SimpleCourseDTO(
    val id: Long,
    val courseName: CourseName
)

fun CourseEntity.toSimpleCourseDto(): SimpleCourseDTO {
    return SimpleCourseDTO(
        this.id,
        this.courseName,
    )
}
