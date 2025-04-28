package de.novatec.itu.studentcrmservice.student.business

import de.novatec.itu.studentcrmservice.student.TestDataProvider.defaultStudentDTO
import de.novatec.itu.studentcrmservice.student.TestDataProvider.defaultStudentEntity
import de.novatec.itu.studentcrmservice.student.TestDataProvider.email
import de.novatec.itu.studentcrmservice.student.TestDataProvider.firstName
import de.novatec.itu.studentcrmservice.student.TestDataProvider.lastName
import de.novatec.itu.studentcrmservice.student.TestDataProvider.studentId
import de.novatec.itu.studentcrmservice.exceptions.EmailAlreadyExistsException
import de.novatec.itu.studentcrmservice.exceptions.StudentNotFoundException
import de.novatec.itu.studentcrmservice.student.business.StudentService
import de.novatec.itu.studentcrmservice.student.persistence.StudentEntity
import de.novatec.itu.studentcrmservice.student.persistence.StudentRepository
import de.novatec.itu.studentcrmservice.student.TestDataProvider.defaultStudentSimpleDTO
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.called
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.Optional

class StudentServiceUnitTest {
    private val studentRepository = mockk<StudentRepository>(relaxed = true)
    private val cut = StudentService(studentRepository)

    @Nested
    inner class GetAllStudents {

        @Test
        fun `getting students`() {
            every { studentRepository.findAll() } returns listOf(defaultStudentEntity)

            val result = cut.findAllStudents()

            result shouldBe listOf(defaultStudentDTO)
            verify { studentRepository.findAll() }
        }

        @Test
        fun `getting empty list of student`() {
            every { studentRepository.findAll() } returns listOf()

            val result = cut.findAllStudents()

            result shouldBe listOf()
            verify { studentRepository.findAll() }
        }
    }

    @Nested
    inner class GetStudentById {

        @Test
        fun `getting student by id`() {
            every { studentRepository.findById(studentId) } returns Optional.of(defaultStudentEntity)

            val result = cut.findStudentById(studentId)

            result shouldBe defaultStudentEntity.toDto()
            verify { studentRepository.findById(studentId) }
        }

        @Test
        fun `throws StudentNotFoundException when student does not exist`() {
            every { studentRepository.findById(studentId) } throws StudentNotFoundException(studentId)

            assertThrows<StudentNotFoundException> {
                cut.findStudentById(studentId)
            }

            verify { studentRepository.findById(studentId) wasNot called }
        }
    }

    @Nested
    inner class CreateNewStudent {

        val studentCaptor = slot<StudentEntity>()

        @BeforeEach
        fun setUp() {
            every { studentRepository.save(capture(studentCaptor)) } answers { studentCaptor.captured }
        }

        @Test
        fun `creates a student`() {
            every { studentRepository.existsByEmailIgnoreCase(email) } returns false

            val result = cut.createStudent(firstName, lastName, email)

            val persistedEntity = studentCaptor.captured
            verify { studentRepository.save(persistedEntity) }
            result shouldBe defaultStudentSimpleDTO
        }

        @Test
        fun `throws an EmailAlreadyExistsException when an Email is already taken`() {
            every { studentRepository.existsByEmailIgnoreCase(email) } returns true

            assertThrows<EmailAlreadyExistsException>() {
                cut.createStudent(firstName, lastName, email)
            }

            verify { studentRepository.save(any()) wasNot called }
        }
/**
        @ParameterizedTest
        @ValueSource(strings = ["", "M", "-Lea", "4343434", "Lea-", "Leadedededededededededededed", "§"])
        fun `throws an InvalidInputException when a Firstname and Lastname have invalid input`(example: String) {

            assertThrows<InvalidInputException> {
                cut.createNewStudent(
                    FirstName(firstName = example),
                    LastName(lastName = example),
                    email
                )
            }

            verify { studentRepository.save(any()) wasNot called }
        }
*/
        @ParameterizedTest
        @ValueSource(strings = ["Jorge", "Jorge Miguel", "Lea-Sophie"])
        fun `does not throw an InvalidInputException when a Firstname and Lastname have valid input`(example: String) {
            every { studentRepository.existsByEmailIgnoreCase(any()) } returns false

            val result = assertDoesNotThrow {
                cut.createStudent(
                    firstName = example,
                    lastName = example,
                    email

                )
            }
            val persistedEntity = studentCaptor.captured
            verify { studentRepository.save(persistedEntity) }
            result shouldBe defaultStudentSimpleDTO.copy(firstName = example, lastName = example, email = email)
        }
/**
        @ParameterizedTest
        @ValueSource(strings = ["", "M", ".username@yahoo.com", "username@yahoo.com.", "username@yahoo..com", "username@yahoo.c", "username@yahoo.corporate"])
        fun `throws an InvalidInputException when an Email has invalid input`(example: String) {

            assertThrows<InvalidInputException>() {
                cut.createStudent(
                    firstName,
                    lastName,
                    email = example
                )

            }

            verify { studentRepository.save(any()) wasNot called }
        }
*/
        @ParameterizedTest
        @ValueSource(strings = ["user@domain.com", "user@domain.co.in", "user.name@domain.com", "user_name@domain.com", "username@yahoo.corporate.in"])
        fun `does not throw an InvalidInputException when an Email has valid input`(example: String) {
            every { studentRepository.existsByEmailIgnoreCase(any()) } returns false

            val result = assertDoesNotThrow {
                cut.createStudent(
                    firstName,
                    lastName,
                    email = example
                )
            }

            val persistedEntity = studentCaptor.captured
            verify { studentRepository.save(persistedEntity) }
            result shouldBe defaultStudentSimpleDTO.copy(firstName = firstName, lastName = lastName, email = example)
        }
    }
}
