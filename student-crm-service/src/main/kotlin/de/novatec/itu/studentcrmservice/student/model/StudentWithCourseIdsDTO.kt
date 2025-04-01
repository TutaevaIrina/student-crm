package de.novatec.itu.studentcrmservice.student.model

data class StudentWithCourseIdsDTO (
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val courses: Set<Long> = emptySet()
    )