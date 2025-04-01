package de.novatec.itu.studentcrmservice.student.api

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class StudentRequestBodyDTO(
    @field:NotBlank(message = "The first name cannot be empty")
    @field:Size(min = 3, max = 20, message = "The first name must be of 3 - 20 characters")
    val firstName: String,

    @field:NotBlank(message = "The last name cannot be empty")
    @field:Size(min = 3, max = 20, message = "The last name must be of 3 - 20 characters")
    val lastName: String,

    @field:NotEmpty
    @field:Email(message = "The email is invalid")
    val email: String
)
