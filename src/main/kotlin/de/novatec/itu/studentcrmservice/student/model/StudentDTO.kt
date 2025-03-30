package de.novatec.itu.studentcrmservice.student.business.model

import de.novatec.itu.studentcrmservice.student.persistence.StudentEntity

data class StudentDTO (
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val courses: Set<Long> = emptySet()
) {
    fun toEntity(): StudentEntity {
        return StudentEntity(
            id = 0,
            firstName = this.firstName,
            lastName = this.lastName,
            email = this.email
        )
    }
}
