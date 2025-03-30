package de.novatec.itu.studentcrmservice.course.business

import de.novatec.itu.studentcrmservice.course.business.model.CourseDTO
import de.novatec.itu.studentcrmservice.course.business.model.CourseSimpleDTO
import de.novatec.itu.studentcrmservice.course.persistence.CourseEntity
import de.novatec.itu.studentcrmservice.course.persistence.CourseRepository
import de.novatec.itu.studentcrmservice.exceptions.*
import de.novatec.itu.studentcrmservice.student.persistence.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val studentRepository: StudentRepository
) {
    fun findAllCourses(): List<CourseDTO> {
        return courseRepository.findAll().map { it.toDto() }
    }

    @Transactional
    fun createCourse(courseName: String): CourseSimpleDTO {
        if (courseRepository.existsByCourseName(courseName)) {
            throw CourseAlreadyExistsException(courseName)
        }
        val courseEntity = CourseEntity(0, courseName)
        return courseRepository.save(courseEntity).toSimpleDTO()
    }

    fun findCourseById(courseId: Long): CourseSimpleDTO {
        return courseRepository.findById(courseId).orElseThrow { CourseNotFoundException(courseId) }.toSimpleDTO()
    }

    fun findCourseByName(courseName: String): List<CourseDTO> {
        val courses = courseRepository.findByNameIgnoreCase(courseName).map { it.toDto() }
        if (courses.isEmpty()) {
            throw CourseNotFoundException(courseName)
        }
        return courses
    }

    @Transactional
    fun updateCourse(courseId: Long, courseName: String): CourseSimpleDTO {
        val courseEntity = courseRepository.findById(courseId).orElseThrow { CourseNotFoundException(courseId) }
        if (courseRepository.existsByCourseName(courseName) && courseName != courseEntity.courseName) {
            throw CourseAlreadyExistsException(courseName)
        }
        courseEntity.courseName = courseName
        return courseRepository.save(courseEntity).toSimpleDTO()
    }

    @Transactional
    fun deleteCourse(courseId: Long) {
        val courseEntity = courseRepository.findById(courseId).orElseThrow { CourseNotFoundException(courseId) }

        if (courseEntity.students.isNotEmpty()) {
            throw StudentEnrolledInCourseException(courseId)
        }
        courseRepository.deleteById(courseId)
    }

    @Transactional
    fun addStudentToCourse(courseId: Long, studentId: Long) {
        val courseEntity = courseRepository.findById(courseId).orElseThrow { CourseNotFoundException(courseId) }
        val studentEntity = studentRepository.findById(studentId).orElseThrow { StudentNotFoundException(studentId) }

        if (courseEntity.students.contains(studentEntity)) {
            throw StudentAlreadyExistsException(courseId, studentId)
        }
        courseEntity.students.add(studentEntity)
        studentEntity.courses.add(courseEntity)

        courseRepository.save(courseEntity)
    }

    @Transactional
    fun removeStudentFromCourse(courseId: Long, studentId: Long) {
        val courseEntity = courseRepository.findById(courseId).orElseThrow { CourseNotFoundException(courseId) }
        val studentEntity = studentRepository.findById(studentId).orElseThrow { StudentNotFoundException(studentId) }

       if (!courseEntity.students.contains(studentEntity)) {
           throw StudentNotFoundException(studentEntity.id)
       }
        courseEntity.students.remove(studentEntity)
        studentEntity.courses.remove(courseEntity)
        courseRepository.save(courseEntity)
    }
}
