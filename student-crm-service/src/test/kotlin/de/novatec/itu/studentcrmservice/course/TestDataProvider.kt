package de.novatec.itu.studentcrmservice.course

import de.novatec.itu.studentcrmservice.course.model.CourseDTO
import de.novatec.itu.studentcrmservice.course.model.CourseSimpleDTO
import de.novatec.itu.studentcrmservice.course.model.CourseWithStudentIdsDTO
import de.novatec.itu.studentcrmservice.course.persistence.CourseEntity
import de.novatec.itu.studentcrmservice.student.persistence.StudentEntity

object TestDataProvider {

    val defaultCourseDTO = CourseDTO(
        id = 0L,
        courseName = "Mathe"
    )

    val defaultSimpleCourseDTO = CourseSimpleDTO(
        id = 0L,
        courseName = "Mathe"
    )

    val defaultCourseEntity = CourseEntity(
        courseName = "Mathe"
    )

    val defaultStudentEntity = StudentEntity(
        firstName = "Maik",
        lastName = "Müller",
        email = "maikmüller@web.de"
    )

    val defaultCourseEntityWithStudents = CourseEntity(
        id = 1L,
        courseName = "Mathe",
        mutableListOf(defaultStudentEntity)
    )

    val courseNameMathe = "Mathe"
    val courseNameIT = "IT"

    val studentName = "Maik Müller"

    val defaultCourseId = 0L

    val defaultStudentId = 0L

    val jsonPropertyIsWrong =
        """a
           {
                "id": 1,
                "courseNa": "Mathe"
           }
        """

    val jsonValueIsBlank =
        """
           {
                "id": 1,
                "courseName": ""
           }
        """

    val jsonValueIsNotValid =
        """
           {
                "id": 1,
                "courseName": "$"
           }
        """

    val courseWithStudentIdsDTO1 = CourseWithStudentIdsDTO(
        defaultCourseId,
        courseNameMathe,
        mutableSetOf()
    )

    val courseWithStudentIdsDTO2 = CourseWithStudentIdsDTO(
        1L,
        courseNameIT,
        mutableSetOf()
    )
}
