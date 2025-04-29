package de.novatec.itu.studentcrmservice.student.persistence

import de.novatec.itu.studentcrmservice.utils.InitializeWithContainerizedPostgreSQL
import de.novatec.itu.studentcrmservice.student.TestDataProvider.email
import de.novatec.itu.studentcrmservice.student.TestDataProvider.firstName
import de.novatec.itu.studentcrmservice.student.TestDataProvider.lastName
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test", "docker")
@InitializeWithContainerizedPostgreSQL
class StudentJPATest(@Autowired private val studentRepository: StudentRepository) {

    @Test
    fun `findByNameIgnoreCase returns students matching first or last name ignoring case`() {
        val student1 = StudentEntity(firstName = firstName, lastName = lastName, email = email)
        val student2 = StudentEntity(firstName = "Lea", lastName = "Müller", email = "lea_mueller@example.com")
        studentRepository.saveAll(listOf(student1, student2))

        val result = studentRepository.findByNameIgnoreCase("er")

        result shouldBe listOf(student1, student2)
    }

    @Test
    fun `existsByEmailIgnoreCase returns true if email exists ignoring case`() {
        val student = StudentEntity(firstName = firstName, lastName = lastName, email = email)
        studentRepository.save(student)

        val exist = studentRepository.existsByEmailIgnoreCase("maik_mueller@web.de")

        exist shouldBe true
    }

    @Test
    fun `existsByEmailIgnoreCase returns false if email does not exist`() {
        val exist = studentRepository.existsByEmailIgnoreCase("unknown@example.com")

        exist shouldBe false
    }
}
