package com.novatec.studentcrmservice.course.api

import com.novatec.studentcrmservice.course.business.CourseService
import com.novatec.studentcrmservice.shared.types.FirstName
import com.novatec.studentcrmservice.shared.types.LastName
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/course-api")
@CrossOrigin(origins = ["http://localhost:4200"])
class CourseController(
    private val courseService: CourseService
) {

    /**
     * Returns all [Course]
     * @return all Courses as [CourseDTO] from the stored [Course]
     */
    @GetMapping("/courses")
    @ResponseStatus(OK)
    fun getAllCourses() = courseService.getAllCourses()

    /**
     * Returns a specific [Course]
     * @param id: [Long] that search for the single [Course]
     * @return a [CourseDTO] from the stored [Course]
     */
    @GetMapping("course/{courseId}")
    @ResponseStatus(OK)
    fun getCourseById(@PathVariable courseId: Long) = courseService.getCourseById(courseId)

    /**
     * Saves a [CourseDTO]
     * @param courseName: [CourseName] that should be added a [Course]
     * @return a [SimpleCourseDTO] from the stored [Course]
     */
    @PostMapping("/course")
    @ResponseStatus(CREATED)
    fun createCourse(@RequestBody body: CourseRequestBodyDTO) =
        courseService.createNewCourse(body.courseName)

    /**
     * Updates a [Course]
     * @param courseId: [Long] that will be updated a [Course]
     * @return a [SimpleCourseDTO] from the stored [Course]
     */
    @PutMapping("/course/{courseId}")
    @ResponseStatus(OK)
    fun updateCourse(@PathVariable courseId: Long, @RequestBody body: CourseRequestBodyDTO) =
        courseService.updateCourse(courseId, body.courseName)

    /**
     * Adds a [Student] to [Course]
     * @param courseId: [Long], firstName: [FirstName], lastname: [LastName] that will be added to an existing stored [Course]
     * @return a [CourseDTO] from the stored [Course]
     */
    @PutMapping("/course/{courseId}/firstName/{firstName}/lastName/{lastName}")
    @ResponseStatus(OK)
    fun addStudentToCourse(@PathVariable courseId: Long, @PathVariable firstName: FirstName, @PathVariable lastName: LastName) =
        courseService.addStudentToCourse(courseId, firstName, lastName)

    /**
     * Deletes a specific [Course]
     * @param id: [Long] that search for the single [Course]
     * @return a [SimpleCourseDTO] from the stored [Course] that will be deleted
     */
    @DeleteMapping("course/{courseId}")
    @ResponseStatus(OK)
    fun deleteCourseById(@PathVariable courseId: Long) = courseService.deleteCourseById(courseId)
}
