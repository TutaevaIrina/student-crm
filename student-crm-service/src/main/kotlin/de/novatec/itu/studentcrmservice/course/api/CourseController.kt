package de.novatec.itu.studentcrmservice.course.api

import de.novatec.itu.studentcrmservice.course.business.CourseService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/course-api")
class CourseController (
    private val courseService: CourseService
) {
    @GetMapping("/courses")
    @ResponseStatus(HttpStatus.OK)
    fun getAllCourses() = courseService.findAllCourses()

    @PostMapping("/course")
    @ResponseStatus(HttpStatus.CREATED)
    fun createCourse(@RequestBody @Valid courseRequestDTO: CourseRequestBodyDTO) =
        courseService.createCourse(courseRequestDTO.courseName)

    @GetMapping("/course/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    fun getCourseById(@PathVariable courseId: Long) =
        courseService.findCourseById(courseId)

    @GetMapping("/courses/{name}")
    @ResponseStatus(HttpStatus.OK)
    fun getCourseByName(@PathVariable name: String) =
        courseService.findCourseByName(name)

    @PutMapping("/course/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    fun updateCourse(@PathVariable courseId: Long, @RequestBody @Valid courseRequestBodyDTO: CourseRequestBodyDTO) =
        courseService.updateCourse(courseId, courseRequestBodyDTO.courseName)

    @DeleteMapping("/course/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCourseById(@PathVariable courseId: Long) =
        courseService.deleteCourse(courseId)

    @PutMapping("/course/{courseId}/student/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun addStudentToCourse(@PathVariable courseId: Long, @PathVariable studentId: Long) =
        courseService.addStudentToCourse(courseId, studentId)

    @DeleteMapping("/course/{courseId}/student/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeStudentFromCourse(@PathVariable courseId: Long, @PathVariable studentId: Long) =
        courseService.removeStudentFromCourse(courseId, studentId)
}
