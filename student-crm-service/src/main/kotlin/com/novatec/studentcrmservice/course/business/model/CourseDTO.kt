package com.novatec.studentcrmservice.course.business.model

import com.novatec.studentcrmservice.course.persistence.CourseEntity
import com.novatec.studentcrmservice.shared.types.CourseName
import com.novatec.studentcrmservice.student.business.model.SimpleStudentDTO
import com.novatec.studentcrmservice.student.business.model.toSimpleStudentDto

data class CourseDTO(
    val id: Long,
    val courseName: CourseName,
    val students: List<SimpleStudentDTO> = listOf()
)

fun CourseEntity.toCourseDto(): CourseDTO {
    val students = this.students.map { it.toSimpleStudentDto() }
    return CourseDTO(
        this.id,
        this.courseName,
        students
    )
}
