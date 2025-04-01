package com.novatec.studentcrmservice.student.business

import com.novatec.studentcrmservice.course.persistence.CourseRepository
import com.novatec.studentcrmservice.shared.errorhandling.CourseAlreadyExistsException
import com.novatec.studentcrmservice.shared.errorhandling.CourseNotFoundException
import com.novatec.studentcrmservice.shared.errorhandling.EmailIsAlreadyExistException
import com.novatec.studentcrmservice.shared.errorhandling.StudentNotFoundException
import com.novatec.studentcrmservice.shared.types.CourseName
import com.novatec.studentcrmservice.shared.types.Email
import com.novatec.studentcrmservice.shared.types.FirstName
import com.novatec.studentcrmservice.shared.types.LastName
import com.novatec.studentcrmservice.student.business.model.*
import com.novatec.studentcrmservice.student.persistence.StudentEntity
import com.novatec.studentcrmservice.student.persistence.StudentRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class StudentService(
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository
) {

    fun getAllStudents(): List<StudentDTO> {
        return studentRepository.findAll().map { studentEntity -> studentEntity.toStudentDto() }
    }

    fun getStudentById(id: Long): StudentDTO {
        val studentEntity = studentRepository.findById(id).orElseThrow { StudentNotFoundException() }
        return studentEntity.toStudentDto()
    }

    @Transactional
    fun createNewStudent(firstName: FirstName, lastName: LastName, email: Email): SimpleStudentDTO {

        if (studentRepository.existsByEmail(email)) {
            throw EmailIsAlreadyExistException()
        }

        return studentRepository.save(StudentEntity(firstName = firstName, lastName = lastName, email = email))
            .toSimpleStudentDto()
    }

    @Transactional
    fun updateStudent(id: Long, firstNameNew: FirstName, lastNameNew: LastName, emailNew: Email): SimpleStudentDTO {
        val studentEntity = studentRepository.findById(id).orElseThrow { StudentNotFoundException() }
        if (studentRepository.existsByEmail(emailNew)) {
            throw EmailIsAlreadyExistException()
        }

        studentEntity.apply {
            firstName.value = firstNameNew.value
            lastName.value = lastNameNew.value
            email.value = emailNew.value
        }

        return studentRepository.save(studentEntity).toSimpleStudentDto()
    }

    @Transactional
    fun addCourseToStudent(studentId: Long, courseName: CourseName): StudentDTO {
        val studentEntity = studentRepository.findById(studentId).orElseThrow { StudentNotFoundException() }
        val courseEntity = courseRepository.findByCourseName(courseName).orElseThrow { CourseNotFoundException() }

        if (studentEntity.courses.contains(courseEntity)) {
            throw CourseAlreadyExistsException(studentId, courseName)
        }

        studentEntity.courses.add(courseEntity)
        courseEntity.students.add(studentEntity)

        return studentRepository.save(studentEntity).toStudentDto()
    }

    @Transactional
    fun deleteStudentById(id: Long): SimpleStudentDTO {
        val studentEntity = studentRepository.findById(id).orElseThrow { StudentNotFoundException() }
        studentRepository.deleteById(id)
        return studentEntity.toSimpleStudentDto()
    }
}
