package de.novatec.itu.studentcrmservice.student

import de.novatec.itu.studentcrmservice.student.model.StudentSimpleDTO
import de.novatec.itu.studentcrmservice.student.model.StudentDTO
import de.novatec.itu.studentcrmservice.student.persistence.StudentEntity

object TestDataProvider {

    val studentId = 0L

    val firstName = "Maik"

    val lastName = "Müller"

    val email = "maikmüller@web.de"

    val defaultStudentDTO = StudentDTO(
        id = 0L,
        firstName = firstName,
        lastName = lastName,
        email = email
    )

    val defaultStudentSimpleDTO = StudentSimpleDTO(
        id = 0L,
        firstName = firstName,
        lastName = lastName,
        email = email
    )

    val defaultStudentEntity = StudentEntity(
        firstName = firstName,
        lastName = lastName,
        email = email
    )
}
