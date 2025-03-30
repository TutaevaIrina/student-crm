package de.novatec.itu.studentcrmservice.student.business
/**
import de.novatec.itu.studentcrmservice.exceptions.EmailAlreadyExistsException
import de.novatec.itu.studentcrmservice.exceptions.StudentNotFoundException
import de.novatec.itu.studentcrmservice.student.api.StudentRequestDTO
import de.novatec.itu.studentcrmservice.student.business.model.StudentDTO
import de.novatec.itu.studentcrmservice.student.persistence.StudentEntity
import de.novatec.itu.studentcrmservice.student.persistence.StudentRepository
import io.mockk.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import java.util.*

class StudentServiceUnitTest {
    private  val studentRepository: StudentRepository = mockk()
    private val studentService = StudentService(studentRepository)
    private val studentDTO = StudentDTO(
        id = 0,
        firstName = "Jessica",
        lastName = "Johnson",
        email = "jessica@johnson.com")

    private val studentRequestDTO1 = StudentRequestDTO(
        firstName = "Jessica",
        lastName = "Johnson",
        email = "jessica@johnson.com")

    private val studentRequestDTO2 = StudentRequestDTO(
        firstName = "John",
        lastName = "Smith",
        email = "john@smith.com")

    private val studentEntity1 = StudentEntity(
        id = 1,
        firstName = "Jessi",
        lastName = "Johnson",
        email = "jessica@johnson.com")

    private val studentEntity2 = StudentEntity(
        id = 2,
        firstName = "John",
        lastName = "Smith",
        email = "john@smith.com")

    private val studentEntityList = listOf(studentEntity1, studentEntity2)

    @Nested
    inner class FindAllStudents {
        @Test
        fun `should return all students`() {
            every { studentRepository.findAll() } returns studentEntityList
            val expectedList = studentEntityList.map { studentEntity -> studentEntity.toDto() }
            val result = studentService.findAllStudents()
            assertEquals(expectedList, result)
        }
    }

    @Nested
    inner class CreateStudent {
        @Test
        fun `should save new student when no duplicate email exists`() {
            every { studentRepository.existsByEmail(studentDTO.email) } returns false
            every { studentRepository.save(any()) } returns studentEntity1
            val createdStudent = studentService.createStudent(studentRequestDTO1.firstName, studentRequestDTO1.lastName, studentRequestDTO1.email)
            assertEquals(studentRequestDTO1.lastName, createdStudent.lastName)
            verify { studentRepository.save(any()) }
        }

        @Test
        fun `should not save new student when duplicate email exists`() {
            every { studentRepository.existsByEmail(any()) } returns true
            assertThrows<EmailAlreadyExistsException> {
                studentService.createStudent(studentRequestDTO1.firstName, studentRequestDTO1.lastName, studentRequestDTO1.email)
            }
            verify(exactly = 0) { studentRepository.save(studentRequestDTO1.toEntity()) }
        }
    }

    @Nested
    inner class FindStudentById {
        @Test
        fun `should find student by id when id exists`() {
            every { studentRepository.findById(any()) } returns Optional.of(studentEntity1)
            val result = studentService.findStudentById(1)
            assertEquals(studentEntity1.toSimpleDto(), result)
        }

        @Test
        fun `should throw exception when no student id exists`() {
            every { studentRepository.findById(any()) } returns Optional.empty()
            assertThrows<StudentNotFoundException> { studentService.findStudentById(3) }
        }
    }

    @Nested
    inner class FindStudentByName{
        @Test
        fun `should return all students with the given name`() {
            every { studentRepository.findByNameIgnoreCase(any()) } returns studentEntityList
            val expectedList = studentEntityList.map { studentEntity -> studentEntity.toDto() }
            val result = studentService.findStudentByName("John")
            assertEquals(expectedList, result)
        }
    }

    @Nested
    inner class UpdateStudent {
        @Test
        fun `should update student when no duplicate email exists`() {
            every { studentRepository.existsByEmail(any()) } returns false
            every { studentRepository.findById(1) } returns Optional.of(studentEntity1)
            every { studentRepository.save(any()) } returns studentEntity1
            val updatedStudent = studentService.updateStudent(1, studentRequestDTO1.firstName, studentRequestDTO1.lastName, studentRequestDTO1.email)
            assertEquals(studentRequestDTO1.firstName, updatedStudent.firstName)
            verify { studentRepository.save(studentEntity1) }
        }

        @Test
        fun `should not update student when duplicate email exists`() {
            every { studentRepository.existsByEmail(any()) } returns true
            every { studentRepository.findById(1) } returns Optional.of(studentEntity1)
            every { studentRepository.save(any()) } returns studentEntity1
            assertThrows<EmailAlreadyExistsException> {
                studentService.updateStudent(1, studentRequestDTO2.firstName, studentRequestDTO2.lastName, studentRequestDTO2.email)
            }
            verify(exactly = 0) { studentRepository.save(any())}
        }
    }

    @Nested
    inner class DeleteStudent {
        @Test
        fun `should delete student by id when id exists`() {
            every { studentRepository.existsById(1) } returns true
            every { studentRepository.deleteById(1) } just Runs
            studentService.deleteStudent(1)
            verify { studentRepository.deleteById(1) }
        }
        @Test
        fun `should not delete student when no id exists`() {
            every { studentRepository.existsById(1) } returns false
            assertThrows<StudentNotFoundException> {
                studentService.deleteStudent(1)
            }
            verify(exactly = 0) { studentRepository.deleteById(1) }
        }
   }
}
*/