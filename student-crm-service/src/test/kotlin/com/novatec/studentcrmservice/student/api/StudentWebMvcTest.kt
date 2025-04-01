package com.novatec.studentcrmservice.student.api

import com.ninjasquad.springmockk.MockkBean
import com.novatec.studentcrmservice.shared.types.Email
import com.novatec.studentcrmservice.shared.types.FirstName
import com.novatec.studentcrmservice.shared.types.LastName
import com.novatec.studentcrmservice.student.TestDataProvider.defaultSimpleStudentDTO
import com.novatec.studentcrmservice.student.TestDataProvider.defaultStudentDTO
import com.novatec.studentcrmservice.student.TestDataProvider.email
import com.novatec.studentcrmservice.student.TestDataProvider.firstName
import com.novatec.studentcrmservice.student.TestDataProvider.lastName
import com.novatec.studentcrmservice.student.business.StudentService
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(StudentController::class)
@MockkBean(StudentService::class)
@AutoConfigureRestDocs(uriPort = 8080)
class StudentWebMvcTest @Autowired constructor(
    private val service: StudentService,
    private val mockMvc: MockMvc
) {

    @Nested
    inner class GetAllStudents {

        @Test
        fun `getting students returns 200 response - happy path`() {

            every { service.getAllStudents() } returns listOf(
                defaultStudentDTO.copy(id = 1L),
                defaultStudentDTO.copy(
                    id = 2L,
                    FirstName(firstName = "Lea"),
                    LastName(lastName = "Fernandes"),
                    Email(email = "l.esteves@web.de")
                )
            )

            mockMvc.perform(get("/student-api/students"))
                .andExpect(status().isOk)
                .andExpect(
                    content().json(
                        """
                        [
                        {
                          "id": 1,
                          "firstName": "Jorge",
                          "lastName": "Esteves",
                          "email": "j.esteves@web.de",
                          "courses": []
                        },
                        {
                          "id": 2,
                          "firstName": "Lea",
                          "lastName": "Fernandes",
                          "email": "l.esteves@web.de",
                          "courses": []
                        }
                        ]    
                        """
                    )
                )
                .andExpect(content().contentType(APPLICATION_JSON))
                .andDo(
                    prettyDocument(
                        "get-all-students",
                        responseFields(getAllStudentResponse)
                    )
                )
        }

        @Test
        fun `parameters are delegated correctly`() {
            every { service.getAllStudents() } returns listOf()

            mockMvc.perform(get("/student-api/students"))

            verify { service.getAllStudents() }
        }
    }

    @Nested
    inner class CreateNewStudent {

        @Test
        fun `creating a student returns 201 response - happy path`() {

            every {
                service.createNewStudent(
                    firstName, lastName, email
                )
            } returns defaultSimpleStudentDTO
            mockMvc.perform(
                post("/student-api/student")
                    .content(
                        """
                            {
                              "id": 1,
                              "firstName": "Jorge",
                              "lastName": "Esteves",
                              "email": "j.esteves@web.de"
                            }
                        """
                    )
                    .contentType(APPLICATION_JSON)
            )
        }

        @Test
        fun `parameters are delegated correctly`() {
            every {
                service.createNewStudent(
                    firstName, lastName, email
                )
            } returns defaultSimpleStudentDTO

            mockMvc.perform(
                post("/student-api/student")
                    .content(
                        """
                            {
                              "id": 1,
                              "firstName": "Jorge",
                              "lastName": "Esteves",
                              "email": "j.esteves@web.de"
                            }
                        """
                    )
                    .contentType(APPLICATION_JSON)
            )

            verify {
                service.createNewStudent(
                    firstName, lastName, email
                )
            }
        }
    }
}
