package com.novatec.studentcrmservice.student.api

import com.novatec.studentcrmservice.shared.types.CourseName
import com.novatec.studentcrmservice.shared.types.FirstName
import com.novatec.studentcrmservice.shared.types.LastName
import com.novatec.studentcrmservice.student.business.StudentService
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/student-api")
@CrossOrigin(origins = ["http://localhost:4200"])
class StudentController(
    private val studentService: StudentService
) {

    /**
     * Returns all [Student]
     * @return all Students
     */
    @GetMapping("/students")
    @ResponseStatus(OK)
    fun getAllStudents() = studentService.getAllStudents()

    /**
     * Returns a specific [Student]
     * @param id: [Long] that search for the single [Student]
     * @return a [StudentDTO] from the stored [Student]
     */
    @GetMapping("/student/{id}")
    @ResponseStatus(OK)
    fun getStudentById(@PathVariable id: Long) = studentService.getStudentById(id)

    /**
     * Saves a [Student]
     * @param firstName: [FirstName], lastname: [LastName], email: [Email] that should be added a [Student]
     * @return a [SimpleStudent] from the stored [Student]
     */
    @PostMapping("/student")
    @ResponseStatus(CREATED)
    fun createStudent(@RequestBody body: StudentRequestBodyDTO) =
        studentService.createNewStudent(body.firstName, body.lastName, body.email)

    /**
     * Updates a [Student]
     * @param id: [Long] that will be updated a [Student]
     * @return a [SimpleStudentDTO] from the stored [Student]
     */
    @PutMapping("/student/{id}")
    @ResponseStatus(OK)
    fun updateStudent(@PathVariable id: Long, @RequestBody body: StudentRequestBodyDTO) =
        studentService.updateStudent(id, body.firstName, body.lastName, body.email)

    /**
     * Adds a [Course] to [Student]
     * @param studentId: [Long], courseName: [courseName] that will be added to an existing stored [Student]
     * @return a [StudentDTO] from the stored [Student]
     */
    @PutMapping("/student/{studentId}/courseName/{courseName}")
    @ResponseStatus(OK)
    fun addCourseToStudent(@PathVariable studentId: Long, @PathVariable courseName: CourseName) =
        studentService.addCourseToStudent(studentId, courseName)

    /**
     * Deletes a specific [Student]
     * @param id: [Long] that search for the single [Student]
     * @return a [SimpleStudentDTO] from the stored [Student] that will be deleted
     */
    @DeleteMapping("student/{id}")
    @ResponseStatus(OK)
    fun deleteStudentById(@PathVariable id: Long) = studentService.deleteStudentById(id)
}
