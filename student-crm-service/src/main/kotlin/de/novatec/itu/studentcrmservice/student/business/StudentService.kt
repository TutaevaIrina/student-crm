package de.novatec.itu.studentcrmservice.student.business

import de.novatec.itu.studentcrmservice.exceptions.EmailAlreadyExistsException
import de.novatec.itu.studentcrmservice.exceptions.StudentNotFoundException
import de.novatec.itu.studentcrmservice.student.model.StudentDTO
import de.novatec.itu.studentcrmservice.student.model.StudentSimpleDTO
import de.novatec.itu.studentcrmservice.student.persistence.StudentEntity
import de.novatec.itu.studentcrmservice.student.persistence.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class StudentService(
    private val studentRepository: StudentRepository
) {
    fun findAllStudents(): List<StudentSimpleDTO> {
        return studentRepository.findAll().map { it.toSimpleDto() }.sortedBy { it.lastName.lowercase(Locale.getDefault()) }
    }

    @Transactional
    fun createStudent(firstName: String, lastName: String, email: String): StudentSimpleDTO {
        if (studentRepository.existsByEmailIgnoreCase(email)) {
            throw EmailAlreadyExistsException()
        }
        return studentRepository.save(StudentEntity(firstName = firstName, lastName = lastName, email = email)).toSimpleDto()
    }

    fun findStudentById(id: Long): StudentDTO {
        return studentRepository.findById(id).orElseThrow { StudentNotFoundException(id) }.toDto()
    }

    fun findStudentByName(name: String): List<StudentSimpleDTO> {
        val students = studentRepository.findByNameIgnoreCase(name).map { it.toSimpleDto() }
        if (students.isEmpty()) {
            throw StudentNotFoundException(name)
        }
        return students
    }

    @Transactional
    fun updateStudent(id: Long, firstName: String, lastName: String, email: String): StudentSimpleDTO {
        val studentEntity = studentRepository.findById(id).orElseThrow { StudentNotFoundException(id) }
        if (studentRepository.existsByEmailIgnoreCase(email) && email != studentEntity.email) {
            throw EmailAlreadyExistsException()
        }

        studentEntity.firstName = firstName
        studentEntity.lastName = lastName
        studentEntity.email = email
        return studentRepository.save(studentEntity).toSimpleDto()
    }

    @Transactional
    fun deleteStudent(id: Long) {
        if (!studentRepository.existsById(id)) {
            throw StudentNotFoundException(id)
        }
        studentRepository.deleteById(id)
    }
}
