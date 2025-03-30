package de.novatec.itu.studentcrmservice.student.api

import de.novatec.itu.studentcrmservice.student.persistence.StudentEntity
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

class StudentRequestDTO(
    @field:NotBlank(message = "The first name cannot be empty")
    @field:Size(min = 3, max = 20, message = "The name must be of 3 - 20 characters")
    val firstName: String,

    @field:NotBlank(message = "The last name cannot be empty")
    @field:Size(min = 3, max = 20, message = "The name must be of 3 - 20 characters")
    val lastName: String,

    @field:NotEmpty
    @field:Email(message = "Invalid email")
    val email: String
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
