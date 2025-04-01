package de.novatec.itu.studentcrmservice.student.api


import de.novatec.itu.studentcrmservice.student.business.StudentService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/student-api")
class StudentController (
    private val studentService: StudentService) {
    @GetMapping("/students")
    @ResponseStatus(HttpStatus.OK)
    fun getAllStudents() = studentService.findAllStudents()

    @PostMapping("/student")
    @ResponseStatus(HttpStatus.CREATED)
    fun createStudent(@Valid @RequestBody studentRequestDTO: StudentRequestBodyDTO) =
        studentService.createStudent(studentRequestDTO.firstName, studentRequestDTO.lastName, studentRequestDTO.email)

    @GetMapping("/student/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    fun getStudentById(@PathVariable studentId: Long) =
        studentService.findStudentById(studentId)

    @GetMapping("/students/{name}")
    @ResponseStatus(HttpStatus.OK)
    fun getStudentByName(@PathVariable name: String) =
        studentService.findStudentByName(name)

    @PutMapping("/student/{studentId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun updateStudent(@PathVariable studentId: Long, @Valid @RequestBody studentRequestDTO: StudentRequestBodyDTO) =
        studentService.updateStudent(studentId, studentRequestDTO.firstName, studentRequestDTO.lastName, studentRequestDTO.email)

    @DeleteMapping("/student/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteStudentById(@PathVariable studentId: Long) =
        studentService.deleteStudent(studentId)
}
