package com.novatec.studentcrmservice.student.business.model

import com.novatec.studentcrmservice.shared.types.Email
import com.novatec.studentcrmservice.shared.types.FirstName
import com.novatec.studentcrmservice.shared.types.LastName
import com.novatec.studentcrmservice.student.persistence.StudentEntity

data class SimpleStudentDTO(
    val id: Long,
    val firstName: FirstName,
    val lastName: LastName,
    val email: Email,
)

fun StudentEntity.toSimpleStudentDto(): SimpleStudentDTO {
    return SimpleStudentDTO(
        this.id,
        this.firstName,
        this.lastName,
        this.email
    )
}
