package de.novatec.itu.studentcrmservice.student.persistence

import de.novatec.itu.studentcrmservice.student.persistence.StudentEntity
import de.novatec.itu.studentcrmservice.student.persistence.StudentRepository
import de.novatec.itu.studentcrmservice.utils.InitializeWithContainerizedPostgreSQL
import de.novatec.itu.studentcrmservice.student.TestDataProvider.defaultStudentSimpleDTO
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
            firstName = "Maik",
            lastName = "Müller",
            email = "maikmüller@web.de"
        )
        val studentCreation = repository.save(studentEntity)

        val listOfStudents = repository.findAll()

        listOfStudents shouldBe listOf(studentCreation)
    }

    @Test
    fun `creates a Student`() {
        val studentEntity = StudentEntity(
            firstName = "Lea",
            lastName = "Müller",
            email = "leamüller@web.de"
        )
        val expectedData = defaultStudentSimpleDTO.copy(
            id = 2,
            firstName = "Lea",
            lastName = "Müller",
            email = "leamüller@web.de"
        )

        val creationOfStudent = repository.save(studentEntity).toSimpleDto()

        creationOfStudent shouldBe expectedData
    }
}
