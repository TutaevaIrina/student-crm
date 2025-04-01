package de.novatec.itu.studentcrmservice.course.business

import de.novatec.itu.studentcrmservice.course.model.CourseDTO
import de.novatec.itu.studentcrmservice.course.model.CourseSimpleDTO
import de.novatec.itu.studentcrmservice.course.model.CourseWithStudentIdsDTO
import de.novatec.itu.studentcrmservice.course.persistence.CourseEntity
import de.novatec.itu.studentcrmservice.course.persistence.CourseRepository
import de.novatec.itu.studentcrmservice.exceptions.*
import de.novatec.itu.studentcrmservice.student.persistence.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val studentRepository: StudentRepository
) {
    fun findCourseInRepositoryById(courseId: Long): CourseEntity {
        return courseRepository.findById(courseId).orElseThrow { CourseNotFoundException(courseId) }
    }

    fun findAllCourses(): List<CourseWithStudentIdsDTO> {
        return courseRepository.findAll().map { it.toCourseWithStudentIdsDTO() }.sortedBy { it.courseName.lowercase(Locale.getDefault()) }
    }

    @Transactional
    fun createCourse(courseName: String): CourseSimpleDTO {
        if (courseRepository.existsByCourseNameIgnoreCase(courseName)) {
            throw CourseAlreadyExistsException(courseName)
        }
        return courseRepository.save(CourseEntity(courseName = courseName)).toSimpleDTO()
    }

    fun findCourseById(courseId: Long): CourseDTO {
        return findCourseInRepositoryById(courseId).toDto()
    }

    fun findCourseByName(courseName: String): List<CourseSimpleDTO> {
        val courses = courseRepository.findByNameIgnoreCase(courseName).map { it.toSimpleDTO() }
        if (courses.isEmpty()) {
            throw CourseNotFoundException(courseName)
        }
        return courses
    }

    @Transactional
    fun updateCourse(courseId: Long, courseName: String): CourseSimpleDTO {
        val courseEntity = findCourseInRepositoryById(courseId)
        if (courseRepository.existsByCourseNameIgnoreCase(courseName) && !courseName.equals(courseEntity.courseName, ignoreCase = true)) {
            throw CourseAlreadyExistsException(courseName)
        }
        courseEntity.courseName = courseName
        return courseRepository.save(courseEntity).toSimpleDTO()
    }

    @Transactional
    fun deleteCourse(courseId: Long) {
        val courseEntity = findCourseInRepositoryById(courseId)

        if (courseEntity.students.isNotEmpty()) {
            throw StudentEnrolledInCourseException(courseId)
        }
        courseRepository.deleteById(courseId)
    }

    @Transactional
    fun addStudentToCourse(courseId: Long, studentId: Long) {
        val courseEntity = findCourseInRepositoryById(courseId)
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
        val courseEntity = findCourseInRepositoryById(courseId)
        val studentEntity = studentRepository.findById(studentId).orElseThrow { StudentNotFoundException(studentId) }

        if (!courseEntity.students.contains(studentEntity)) {
            throw StudentNotEnrolledInCourseException(courseEntity.id, studentEntity.id)
        }
        courseEntity.students.remove(studentEntity)
        studentEntity.courses.remove(courseEntity)
        courseRepository.save(courseEntity)
    }
}
