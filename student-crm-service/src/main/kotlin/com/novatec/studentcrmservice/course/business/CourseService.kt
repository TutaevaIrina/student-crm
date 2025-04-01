package com.novatec.studentcrmservice.course.business

import com.novatec.studentcrmservice.course.business.model.*
import com.novatec.studentcrmservice.course.persistence.CourseEntity
import com.novatec.studentcrmservice.course.persistence.CourseRepository
import com.novatec.studentcrmservice.shared.errorhandling.*
import com.novatec.studentcrmservice.shared.types.CourseName
import com.novatec.studentcrmservice.shared.types.FirstName
import com.novatec.studentcrmservice.shared.types.LastName
import com.novatec.studentcrmservice.student.persistence.StudentRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val studentRepository: StudentRepository
) {

    fun getAllCourses(): List<CourseDTO> {
        return courseRepository.findAll().map { courseEntity -> courseEntity.toCourseDto() }
    }

    fun getCourseById(id: Long): CourseDTO {
        val courseEntity = courseRepository.findById(id).orElseThrow { CourseNotFoundException() }
        return courseEntity.toCourseDto()
    }

    @Transactional
    fun createNewCourse(courseName: CourseName): SimpleCourseDTO {

        if (courseRepository.existsByCourseName(courseName)) {
            throw CoursenameIsAlreadyExistException()
        }

        return courseRepository.save(CourseEntity(courseName = courseName)).toSimpleCourseDto()
    }

    @Transactional
    fun updateCourse(id: Long, courseNameNew: CourseName): SimpleCourseDTO {
        val courseEntity = courseRepository.findById(id).orElseThrow { CourseNotFoundException() }

        if (courseRepository.existsByCourseName(courseNameNew)) {
            throw CoursenameIsAlreadyExistException()
        }

        courseEntity.courseName = courseNameNew
        return courseRepository.save(courseEntity).toSimpleCourseDto()
    }

    @Transactional
    fun addStudentToCourse(courseId: Long, firstName: FirstName, lastName: LastName): CourseDTO {
        val courseEntity = courseRepository.findById(courseId).orElseThrow { CourseNotFoundException() }
        val studentEntity =
            studentRepository.findByFirstNameAndLastName(firstName, lastName).orElseThrow { StudentNotFoundException() }

        if (courseEntity.students.contains(studentEntity)) {
            throw StudentAlreadyExistsException(courseId, firstName, lastName)
        }

        courseEntity.students.add(studentEntity)
        studentEntity.courses.add(courseEntity)

        return courseRepository.save(courseEntity).toCourseDto()
    }

    @Transactional
    fun deleteCourseById(id: Long): SimpleCourseDTO {
        val courseEntity = courseRepository.findById(id).orElseThrow { CourseNotFoundException() }

        if (courseEntity.students.isNotEmpty()) {
            throw StudentDataInUseException(id)
        }
        courseRepository.deleteById(id)
        return courseEntity.toSimpleCourseDto()
    }
}
