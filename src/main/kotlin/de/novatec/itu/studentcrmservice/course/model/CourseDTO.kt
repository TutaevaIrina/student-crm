package de.novatec.itu.studentcrmservice.course.business.model

import de.novatec.itu.studentcrmservice.course.persistence.CourseEntity

data class CourseDTO(
    val id: Long,
    val courseName: String,
    val students: Set<Long> = emptySet()
) {
    fun toEntity(): CourseEntity {
        return CourseEntity(
            id = 0,
            courseName = this.courseName
        )
    }
}
