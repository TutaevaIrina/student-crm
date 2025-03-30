package de.novatec.itu.studentcrmservice.student.business.model

data class StudentSimpleDTO(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
)