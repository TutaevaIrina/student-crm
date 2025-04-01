package com.novatec.studentcrmservice.course.api

import com.novatec.studentcrmservice.shared.types.CourseName

data class CourseRequestBodyDTO(
    val courseName: CourseName
)
