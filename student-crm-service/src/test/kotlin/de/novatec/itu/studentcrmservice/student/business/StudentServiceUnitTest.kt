package de.novatec.itu.studentcrmservice.student.business

import de.novatec.itu.studentcrmservice.student.TestDataProvider.defaultStudentEntity
import de.novatec.itu.studentcrmservice.student.TestDataProvider.email
import de.novatec.itu.studentcrmservice.student.TestDataProvider.firstName
import de.novatec.itu.studentcrmservice.student.TestDataProvider.lastName
import de.novatec.itu.studentcrmservice.student.TestDataProvider.studentId
import de.novatec.itu.studentcrmservice.exceptions.EmailAlreadyExistsException
import de.novatec.itu.studentcrmservice.exceptions.StudentNotFoundException
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

            result shouldBe listOf(defaultStudentSimpleDTO)
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

            verify { studentRepository.findById(studentId) }
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
    }
}
