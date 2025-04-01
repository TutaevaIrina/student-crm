package de.novatec.itu.studentcrmservice.course.model

import de.novatec.itu.studentcrmservice.student.model.StudentSimpleDTO
import de.novatec.itu.studentcrmservice.student.persistence.StudentEntity

data class CourseDTO(
    val id: Long,
    val courseName: String,
    val students: List<StudentSimpleDTO> = emptyList()
)
