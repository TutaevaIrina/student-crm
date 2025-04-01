package com.novatec.studentcrmservice.student.business

import com.novatec.studentcrmservice.course.persistence.CourseRepository
import com.novatec.studentcrmservice.shared.errorhandling.EmailIsAlreadyExistException
import com.novatec.studentcrmservice.shared.errorhandling.InvalidInputException
import com.novatec.studentcrmservice.shared.errorhandling.StudentNotFoundException
import com.novatec.studentcrmservice.shared.types.Email
import com.novatec.studentcrmservice.shared.types.FirstName
import com.novatec.studentcrmservice.shared.types.LastName
import com.novatec.studentcrmservice.student.TestDataProvider.defaultSimpleStudentDTO
import com.novatec.studentcrmservice.student.TestDataProvider.defaultStudentDTO
import com.novatec.studentcrmservice.student.TestDataProvider.defaultStudentEntity
import com.novatec.studentcrmservice.student.TestDataProvider.email
import com.novatec.studentcrmservice.student.TestDataProvider.firstName
import com.novatec.studentcrmservice.student.TestDataProvider.lastName
import com.novatec.studentcrmservice.student.TestDataProvider.studentId
import com.novatec.studentcrmservice.student.business.model.toStudentDto
import com.novatec.studentcrmservice.student.persistence.StudentEntity
import com.novatec.studentcrmservice.student.persistence.StudentRepository
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
    private val courseRepository = mockk<CourseRepository>(relaxed = true)
    private val cut = StudentService(studentRepository, courseRepository)

    @Nested
    inner class GetAllStudents {

        @Test
        fun `getting students`() {
            every { studentRepository.findAll() } returns listOf(defaultStudentEntity)

            val result = cut.getAllStudents()

            result shouldBe listOf(defaultStudentDTO)
            verify { studentRepository.findAll() }
        }

        @Test
        fun `getting empty list of student`() {
            every { studentRepository.findAll() } returns listOf()

            val result = cut.getAllStudents()

            result shouldBe listOf()
            verify { studentRepository.findAll() }
        }
    }

    @Nested
    inner class GetStudentById {

        @Test
        fun `getting student by id`() {
            every { studentRepository.findById(studentId) } returns Optional.of(defaultStudentEntity)

            val result = cut.getStudentById(studentId)

            result shouldBe defaultStudentEntity.toStudentDto()
            verify { studentRepository.findById(studentId) }
        }

        @Test
        fun `throws StudentNotFoundException when student does not exist`() {
            every { studentRepository.findById(studentId) } throws StudentNotFoundException()

            assertThrows<StudentNotFoundException> {
                cut.getStudentById(studentId)
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
            every { studentRepository.existsByEmail(email) } returns false

            val result = cut.createNewStudent(firstName, lastName, email)

            val persistedEntity = studentCaptor.captured
            verify { studentRepository.save(persistedEntity) }
            result shouldBe defaultSimpleStudentDTO
            persistedEntity.firstName shouldBe defaultSimpleStudentDTO.firstName
            persistedEntity.lastName shouldBe defaultSimpleStudentDTO.lastName
            persistedEntity.email shouldBe defaultSimpleStudentDTO.email
        }

        @Test
        fun `throws an EmailIsAlreadyExistException when an Email is already taken`() {
            every { studentRepository.existsByEmail(email) } returns true

            assertThrows<EmailIsAlreadyExistException>() {
                cut.createNewStudent(firstName, lastName, email)
            }

            verify { studentRepository.save(any()) wasNot called }
        }

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

        @ParameterizedTest
        @ValueSource(strings = ["Jorge", "Jorge Miguel", "Lea-Sophie"])
        fun `does not throw an InvalidInputException when a Firstname and Lastname have valid input`(example: String) {
            every { studentRepository.existsByEmail(any()) } returns false

            val result = assertDoesNotThrow {
                cut.createNewStudent(
                    FirstName(firstName = example),
                    LastName(lastName = example),
                    email

                )
            }
            val persistedEntity = studentCaptor.captured
            verify { studentRepository.save(persistedEntity) }
            result shouldBe defaultSimpleStudentDTO.copy(firstName = FirstName(example), lastName = LastName(example), email = email)
            persistedEntity.firstName shouldBe FirstName(firstName = example)
            persistedEntity.lastName shouldBe LastName(lastName = example)
            persistedEntity.email shouldBe email
        }

        @ParameterizedTest
        @ValueSource(strings = ["", "M", ".username@yahoo.com", "username@yahoo.com.", "username@yahoo..com", "username@yahoo.c", "username@yahoo.corporate"])
        fun `throws an InvalidInputException when an Email has invalid input`(example: String) {

            assertThrows<InvalidInputException>() {
                cut.createNewStudent(
                    firstName,
                    lastName,
                    Email(email = example)
                )

            }

            verify { studentRepository.save(any()) wasNot called }
        }

        @ParameterizedTest
        @ValueSource(strings = ["user@domain.com", "user@domain.co.in", "user.name@domain.com", "user_name@domain.com", "username@yahoo.corporate.in"])
        fun `does not throw an InvalidInputException when an Email has valid input`(example: String) {
            every { studentRepository.existsByEmail(any()) } returns false

            val result = assertDoesNotThrow {
                cut.createNewStudent(
                    firstName,
                    lastName,
                    Email(email = example)
                )
            }

            val persistedEntity = studentCaptor.captured
            verify { studentRepository.save(persistedEntity) }
            result shouldBe defaultSimpleStudentDTO.copy(firstName = firstName, lastName = lastName, email = Email(example))
            persistedEntity.firstName shouldBe firstName
            persistedEntity.lastName shouldBe lastName
            persistedEntity.email shouldBe Email(email = example)
        }
    }
}
