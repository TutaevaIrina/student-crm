package de.novatec.itu.studentcrmservice.course.model

data class CourseWithStudentIdsDTO (
    val id: Long,
    val courseName: String,
    val students: Set<Long> = emptySet()
)