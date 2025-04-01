package de.novatec.itu.studentcrmservice.student.model

import de.novatec.itu.studentcrmservice.course.model.CourseSimpleDTO

data class StudentDTO (
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val courses: List<CourseSimpleDTO> = emptyList()
)
