package de.novatec.itu.studentcrmservice.course.api

import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.snippet.Snippet

fun prettyDocument(id: String, vararg snippets: Snippet): RestDocumentationResultHandler =
    document(id, preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()), *snippets)

val getCoursesResponse: List<FieldDescriptor> = listOf(
    fieldWithPath("[].id")
        .type("Long")
        .description("The id of the course."),
    fieldWithPath("[].courseName")
        .type("String")
        .description(
            buildString {
                appendLine("The course name of course.")
                appendLine()
                appendLine("Type: `String` +")
                appendLine("Pattern: `[a-zA-Z]{2,20})([- ][a-zA-Z]{2,20})*` +")
                appendLine("Examples: `Mathe`, `Mathe-Hoch2`, `Mathe2` etc.")
            }
        ),
    fieldWithPath("[].students")
        .type("Set<Long>")
        .description("A list of students which is from type Set<Long> that represents the ids of students.")
)

val courseRequest: List<FieldDescriptor> = listOf(
    fieldWithPath("courseName")
        .type("String")
        .description(
            buildString {
                appendLine("The course name of a course.")
                appendLine()
                appendLine("Type: `String` +")
                appendLine("Pattern: `[a-zA-Z]{2,20})([- ][a-zA-Z]{2,20})*` +")
                appendLine("Examples: `Mathe`, `Mathe-Hoch2`, `Mathe2` etc.")
            }
        )
)

val courseResponse: List<FieldDescriptor> = listOf(
    fieldWithPath("id")
        .type("Long")
        .description(
            buildString {
                appendLine("The id of the course as a Long number.")
                appendLine()
                appendLine("Type: `Long` +")
                appendLine("The id will be given sequentially by the database itself.")
            }
        ),
    fieldWithPath("courseName")
        .type("String")
        .description(
            buildString {
                appendLine("The course name of a course.")
                appendLine()
                appendLine("Type: `String` +")
                appendLine("Pattern: `[a-zA-Z]{2,20})([- ][a-zA-Z]{2,20})*` +")
                appendLine("Examples: `Mathe`, `Mathe-Hoch2`, `Mathe2` etc.")
            }
        )
)

val getCourseByIdResponse: List<FieldDescriptor> = listOf(
    fieldWithPath("id")
        .type("Long")
        .description(
            buildString {
                appendLine("The id of the course as a Long number.")
                appendLine()
                appendLine("Type: `Long` +")
                appendLine("The id will be given sequentially by the database itself.")
            }
        ),
    fieldWithPath("courseName")
        .type("String")
        .description(
            buildString {
                appendLine("The course name of a course.")
                appendLine()
                appendLine("Type: `String` +")
                appendLine("Pattern: `[a-zA-Z]{2,20})([- ][a-zA-Z]{2,20})*` +")
                appendLine("Examples: `Mathe`, `Mathe-Hoch2`, `Mathe2` etc.")
            }
        ),
    fieldWithPath("students.[]")
        .type("List<StudentSimpleDTO>")
        .description("A list of students which is from type List<StudentSimpleDTO> that represents the entity of students.")
)

val getCourseByNameResponse: List<FieldDescriptor> = listOf(
    fieldWithPath("[].id")
        .type("Long")
        .description(
            buildString {
                appendLine("The id of the course as a Long number.")
                appendLine()
                appendLine("Type: `Long` +")
                appendLine("The id will be given sequentially by the database itself.")
            }
        ),
    fieldWithPath("[].courseName")
        .type("String")
        .description(
            buildString {
                appendLine("The course name of a course.")
                appendLine()
                appendLine("Type: `String` +")
                appendLine("Pattern: `[a-zA-Z]{2,20})([- ][a-zA-Z]{2,20})*` +")
                appendLine("Examples: `Mathe`, `Mathe-Hoch2`, `Mathe2` etc.")
            }
        )
)

val courseIdParam: ParameterDescriptor =
    RequestDocumentation.parameterWithName("courseId")
        .description(
            buildString {
                appendLine("The id of the course as a Long number.")
                appendLine()
                appendLine("Type: `Long` +")
                appendLine("The id will be given sequentially by the database itself.")
            }
        )

val courseNameParam: ParameterDescriptor =
    RequestDocumentation.parameterWithName("name")
        .description(
            buildString {
                appendLine("The name of the course or part of the name as a String.")
                appendLine()
                appendLine("Type `String` +")
                appendLine("Examples: `Mathe`, `ma`, `he` etc.")
            }
        )

val studentIdParam: ParameterDescriptor =
    RequestDocumentation.parameterWithName("studentId")
        .description(
            buildString {
                appendLine("The id of the student as a Long number.")
                appendLine()
                appendLine("Type: `Long` +")
                appendLine("The id will be given sequentially by the database itself.")
            }
        )