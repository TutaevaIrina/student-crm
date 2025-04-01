package com.novatec.studentcrmservice.shared.types

const val COURSE_NAME_IDENTIFIER = "courseName"
val COURSE_NAME_FIELD_REGEX = Regex("""([a-zA-Z0-9_-]{2,25})( [a-zA-Z0-9]{2,25})*""")

class CourseName(
    courseName: String
) : BasicTextField(courseName) {
    init {
        verifyField(COURSE_NAME_IDENTIFIER, COURSE_NAME_FIELD_REGEX)
    }
}

fun String.toCourseName() = CourseName(this)