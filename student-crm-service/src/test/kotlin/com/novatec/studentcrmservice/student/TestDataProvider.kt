package com.novatec.studentcrmservice.student

import com.novatec.studentcrmservice.shared.types.Email
import com.novatec.studentcrmservice.shared.types.FirstName
import com.novatec.studentcrmservice.shared.types.LastName
import com.novatec.studentcrmservice.student.business.model.SimpleStudentDTO
import com.novatec.studentcrmservice.student.business.model.StudentDTO
import com.novatec.studentcrmservice.student.persistence.StudentEntity

object TestDataProvider {

    val defaultStudentDTO = StudentDTO(
        id = 0L,
        firstName = FirstName("Jorge"),
        lastName = LastName("Esteves"),
        email = Email("j.esteves@web.de")
    )

    val defaultSimpleStudentDTO = SimpleStudentDTO(
        id = 0L,
        firstName = FirstName("Jorge"),
        lastName = LastName("Esteves"),
        email = Email("j.esteves@web.de")
    )

    val defaultStudentEntity = StudentEntity(
        firstName = FirstName("Jorge"),
        lastName = LastName("Esteves"),
        email = Email("j.esteves@web.de")
    )

    val studentId = 0L

    val firstName = FirstName("Jorge")

    val lastName = LastName("Esteves")

    val email = Email("j.esteves@web.de")
}
