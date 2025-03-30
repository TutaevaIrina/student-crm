package de.novatec.itu.studentcrmservice.student.business

import de.novatec.itu.studentcrmservice.exceptions.EmailAlreadyExistsException
import de.novatec.itu.studentcrmservice.exceptions.StudentNotFoundException
import de.novatec.itu.studentcrmservice.student.business.model.StudentDTO
import de.novatec.itu.studentcrmservice.student.business.model.StudentSimpleDTO
import de.novatec.itu.studentcrmservice.student.persistence.StudentEntity
import de.novatec.itu.studentcrmservice.student.persistence.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StudentService(
    private val studentRepository: StudentRepository
) {
    fun findAllStudents(): List<StudentDTO> {
        return studentRepository.findAll().map { it.toDto() }
    }

    @Transactional
    fun createStudent(firstName: String, lastName: String, email: String): StudentSimpleDTO {
        if (studentRepository.existsByEmail(email)) {
            throw EmailAlreadyExistsException()
        }
        val studentEntity = StudentEntity(-1, firstName, lastName, email)

        return studentRepository.save(studentEntity).toSimpleDto()
    }

    fun findStudentById(id: Long): StudentSimpleDTO {
        return studentRepository.findById(id).orElseThrow { StudentNotFoundException(id) }.toSimpleDto()
    }

    fun findStudentByName(name: String): List<StudentDTO> {
        val students = studentRepository.findByNameIgnoreCase(name).map { it.toDto() }
        if (students.isEmpty()) {
            throw StudentNotFoundException(name)
        }
        return students
    }

    @Transactional
    fun updateStudent(id: Long, firstName: String, lastName: String, email: String): StudentSimpleDTO {
        val studentEntity = studentRepository.findById(id).orElseThrow { StudentNotFoundException(id) }
        if (studentRepository.existsByEmail(email) && email != studentEntity.email) {
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
