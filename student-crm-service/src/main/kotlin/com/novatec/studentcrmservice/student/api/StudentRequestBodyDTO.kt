package com.novatec.studentcrmservice.student.api

import com.novatec.studentcrmservice.shared.types.Email
import com.novatec.studentcrmservice.shared.types.FirstName
import com.novatec.studentcrmservice.shared.types.LastName

data class StudentRequestBodyDTO(
    val firstName: FirstName,
    val lastName: LastName,
    val email: Email
)
