package de.novatec.itu.studentcrmservice.course

import de.novatec.itu.studentcrmservice.course.api.CourseRequestBodyDTO
import de.novatec.itu.studentcrmservice.course.model.CourseDTO
import de.novatec.itu.studentcrmservice.course.model.CourseSimpleDTO
import de.novatec.itu.studentcrmservice.course.model.CourseWithStudentIdsDTO
import de.novatec.itu.studentcrmservice.course.persistence.CourseEntity
import de.novatec.itu.studentcrmservice.student.persistence.StudentEntity

object TestDataProvider {
    val defaultCourseRequestBody = CourseRequestBodyDTO(courseName = "English")
    val defaultCourseSimpleDTO = CourseSimpleDTO(id = 1, courseName = "English")
    val defaultCourseWithStudentIdsDTO = CourseWithStudentIdsDTO(id = 1, courseName = "English", students = setOf())
    val defaultCourseDTO = CourseDTO(id = 1L, courseName = "English")
    val defaultCourseNameEnglish = "English"
    val defaultCourseNameSpanish = "Spanish"
    val defaultCourseId = 1L
    val defaultStudentId = 1L
    val defaultCourseEntityList = listOf(
        CourseEntity(1L, "English"),
        CourseEntity(
            2L, "Spanish", mutableListOf(
                StudentEntity(id = 1, "Mark", "Miller", "mark@miller.com")
            )
        )
    )

    val jsonPropertyIsBlank = """
        {
            "id": 1,
            "courseName": ""
        }        
    """
    val jsonPropertyIsInvalid = """
       {
            "id": 1,
            "courseName": "$"
       } 
    """

    val jsonPropertyIsMissing = """
        {
            "id": 1,
            "coursNam": "Mathe"
       }        
    """
}
