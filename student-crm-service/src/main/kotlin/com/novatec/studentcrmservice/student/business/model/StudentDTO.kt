package com.novatec.studentcrmservice.student.business.model

import com.novatec.studentcrmservice.course.business.model.SimpleCourseDTO
import com.novatec.studentcrmservice.course.business.model.toSimpleCourseDto
import com.novatec.studentcrmservice.shared.types.Email
import com.novatec.studentcrmservice.shared.types.FirstName
import com.novatec.studentcrmservice.shared.types.LastName
import com.novatec.studentcrmservice.student.persistence.StudentEntity

data class StudentDTO(
    val id: Long,
    val firstName: FirstName,
    val lastName: LastName,
    val email: Email,
    val courses: List<SimpleCourseDTO> = listOf()
)

fun StudentEntity.toStudentDto(): StudentDTO {
    val courses = this.courses.map { it.toSimpleCourseDto() }
    return StudentDTO(
        this.id,
        this.firstName,
        this.lastName,
        this.email,
        courses
    )
}
