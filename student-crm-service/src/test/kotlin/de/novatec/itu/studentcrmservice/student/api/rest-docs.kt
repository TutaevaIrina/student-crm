package de.novatec.itu.studentcrmservice.student.api

import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.snippet.Snippet

fun prettyDocument(id: String, vararg snippets: Snippet): RestDocumentationResultHandler =
    document(id, preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()), *snippets)

val getAllStudentsResponse: List<FieldDescriptor> = listOf(
    fieldWithPath("[].id")
        .type("Long")
        .description("The id of the Student."),
    fieldWithPath("[].firstName")
        .type("String")
        .description(
            buildString {
                appendLine("The firstname of Student.")
                appendLine()
                appendLine("Type: `String` +")
                appendLine("Pattern: `[a-zA-Z]{2,20})([- ][a-zA-Z]{2,20})*` +")
                appendLine("Examples: `Max`, `Anna Marie`, `Laura-Sophie` etc.")
            }
        ),
    fieldWithPath("[].lastName")
        .type("String")
        .description(
            buildString {
                appendLine("The firstname of Student.")
                appendLine()
                appendLine("Type: `String` +")
                appendLine("Pattern: `[a-zA-Z]{2,20})([- ][a-zA-Z]{2,20})*` +")
                appendLine("Examples: `Mustermann`, `Fernandes Esteves` etc.")
            }
        ),
    fieldWithPath("[].email")
        .type("String")
        .description(
            buildString {
                appendLine("Country code retrieved from 'warranty-user-settings'.")
                appendLine()
                appendLine("Type: `String` +")
                appendLine("Pattern: `[\\w!#\$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#\$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}\$` +")
                appendLine("Examples: `user@domain.com`, `user.name@domain.com`, `user_name@domain.com`, `username@yahoo.corporate.in` etc.")
            }
        )
)
