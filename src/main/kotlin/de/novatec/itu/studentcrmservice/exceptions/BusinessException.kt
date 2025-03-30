package de.novatec.itu.studentcrmservice.exceptions

sealed class BusinessException(override val message: String) : RuntimeException()
class StudentNotFoundException : BusinessException {
    constructor(studentId: Long): super("Student with id $studentId not found")
    constructor(studentName: String): super("Student with name $studentName not found")
}
class CourseNotFoundException : BusinessException {
    constructor(courseId: Long): super("Course with id $courseId not found")
    constructor(courseName: String): super("Course with name $courseName not found")
}
class EmailAlreadyExistsException : BusinessException("Email already exists")
class CourseAlreadyExistsException(courseName: String) : BusinessException("Course with name $courseName already exists")
class StudentAlreadyExistsException(courseId: Long, studentId: Long) : BusinessException("Student with id $studentId already exists in course with id $courseId")
class StudentEnrolledInCourseException(courseId: Long) : BusinessException("Course with id $courseId cannot be deleted because there are students enrolled in it")