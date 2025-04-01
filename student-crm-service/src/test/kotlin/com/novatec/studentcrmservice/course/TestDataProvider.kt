package com.novatec.studentcrmservice.course

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.novatec.studentcrmservice.course.business.model.CourseDTO
import com.novatec.studentcrmservice.course.business.model.SimpleCourseDTO
import com.novatec.studentcrmservice.course.persistence.CourseEntity
import com.novatec.studentcrmservice.shared.types.CourseName
import com.novatec.studentcrmservice.shared.types.Email
import com.novatec.studentcrmservice.shared.types.FirstName
import com.novatec.studentcrmservice.shared.types.LastName
import com.novatec.studentcrmservice.student.persistence.StudentEntity

object TestDataProvider {

    val defaultCourseDTO = CourseDTO(
        id = 0L,
        courseName = CourseName("Mathe")
    )

    val defaultSimpleCourseDTO = SimpleCourseDTO(
        id = 0L,
        courseName = CourseName("Mathe")
    )

    val defaultCourseEntity = CourseEntity(
        courseName = CourseName("Mathe")
    )

    val defaultStudentEntity = StudentEntity(
        firstName = FirstName("Jorge"),
        lastName = LastName("Esteves"),
        email = Email("je@web.de")
    )

    val courseNameMathe = CourseName("Mathe")
    val courseNameIT = CourseName("IT")

    val lastName = LastName("Esteves")

    val firstName = FirstName("Jorge")

    val defaultCourseId = 0L

    val jsonPropertyIsMissing =
        """
           {
                "id": 1,
                "courseNa": "Mathe"
           }
        """

    val jsonPropertyIsBlank =
        """
           {
                "id": 1,
                "courseName": ""
           }
        """

    val jsonPropertyIsNotValid =
        """
           {
                "id": 1,
                "courseName": "$"
           }
        """
}
