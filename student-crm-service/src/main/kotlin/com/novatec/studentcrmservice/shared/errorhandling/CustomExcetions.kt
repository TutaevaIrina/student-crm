package com.novatec.studentcrmservice.shared.errorhandling

import com.novatec.studentcrmservice.shared.types.CourseName
import com.novatec.studentcrmservice.shared.types.FirstName
import com.novatec.studentcrmservice.shared.types.LastName
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException

sealed class BusinessException(
    override val message: String
) : RuntimeException()

class CoursenameIsAlreadyExistException : BusinessException(
    "The enter coursename is already taken."
)

class CourseNotFoundException : BusinessException(
    "Course was not found"
)

class CourseAlreadyExistsException(studentId: Long, courseName: CourseName) : BusinessException(
    "Course with courseName $courseName already exists in student with id $studentId"
)

class StudentNotFoundException() : BusinessException(
    "Student was not found."
)

class StudentAlreadyExistsException(courseId: Long, firstName: FirstName, lastName: LastName) : BusinessException(
    "Student with firstName $firstName and lastName $lastName already exists in course with id $courseId"
)

class StudentDataInUseException(id: Long): BusinessException(
    "Course with id $id cannot be delete because Students are still in use"
)

class EmailIsAlreadyExistException : BusinessException(
    "The enter Email is already taken."
)

class InvalidInputException(message: String) : BusinessException(message)
