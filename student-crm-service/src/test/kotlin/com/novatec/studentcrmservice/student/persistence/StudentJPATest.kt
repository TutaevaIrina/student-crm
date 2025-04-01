package com.novatec.studentcrmservice.student.persistence

import com.novatec.studentcrmservice.utils.InitializeWithContainerizedPostgreSQL
import com.novatec.studentcrmservice.shared.types.Email
import com.novatec.studentcrmservice.shared.types.FirstName
import com.novatec.studentcrmservice.shared.types.LastName
import com.novatec.studentcrmservice.student.TestDataProvider.defaultSimpleStudentDTO
import com.novatec.studentcrmservice.student.business.model.toSimpleStudentDto
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test", "docker")
@InitializeWithContainerizedPostgreSQL
class StudentJPATest(@Autowired private val repository: StudentRepository) {

    @Test
    fun `find all Students`() {
        val studentEntity = StudentEntity(
            firstName = FirstName("Jorge"),
            lastName = LastName("Esteves"),
            email = Email("j.esteves@web.de")
        )
        val studentCreation = repository.save(studentEntity)

        val listOfStudents = repository.findAll()

        listOfStudents shouldBe listOf(studentCreation)
    }

    @Test
    fun `creates a Student`() {
        val studentEntity = StudentEntity(
            firstName = FirstName("Lea"),
            lastName = LastName("Esteves"),
            email = Email("l.esteves@web.de")
        )
        val expectedData = defaultSimpleStudentDTO.copy(
            id = 2,
            FirstName(firstName = "Lea"),
            LastName(lastName = "Esteves"),
            Email(email = "l.esteves@web.de")
        )

        val creationOfStudent = repository.save(studentEntity).toSimpleStudentDto()

        creationOfStudent shouldBe expectedData
    }
}
