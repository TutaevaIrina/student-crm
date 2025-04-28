package de.novatec.itu.studentcrmservice.student.api

import com.ninjasquad.springmockk.MockkBean
import de.novatec.itu.studentcrmservice.student.TestDataProvider.email
import de.novatec.itu.studentcrmservice.student.TestDataProvider.firstName
import de.novatec.itu.studentcrmservice.student.TestDataProvider.lastName
import de.novatec.itu.studentcrmservice.student.api.StudentController
import de.novatec.itu.studentcrmservice.student.business.StudentService
import de.novatec.itu.studentcrmservice.student.TestDataProvider.defaultStudentSimpleDTO
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

            every { service.findAllStudents() } returns listOf(
                defaultStudentSimpleDTO.copy(id = 1L),
                defaultStudentSimpleDTO.copy(
                    id = 2L,
                    firstName = "Lea",
                    lastName = "Müller",
                    email = "leamüller@web.de"
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
                          "firstName": "Maik",
                          "lastName": "Müller",
                          "email": "maikmüller@web.de",
                          "courses": []
                        },
                        {
                          "id": 2,
                          "firstName": "Lea",
                          "lastName": "Müller",
                          "email": "leamüller@web.de",
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
            every { service.findAllStudents() } returns listOf()

            mockMvc.perform(get("/student-api/students"))

            verify { service.findAllStudents() }
        }
    }

    @Nested
    inner class CreateNewStudent {

        @Test
        fun `creating a student returns 201 response - happy path`() {

            every {
                service.createStudent(
                    firstName, lastName, email
                )
            } returns defaultStudentSimpleDTO
            mockMvc.perform(
                post("/student-api/student")
                    .content(
                        """
                            {
                              "id": 1,
                              "firstName": "Maik",
                              "lastName": "Müller",
                              "email": "maikmüller@web.de"
                            }
                        """
                    )
                    .contentType(APPLICATION_JSON)
            )
        }

        @Test
        fun `parameters are delegated correctly`() {
            every {
                service.createStudent(
                    firstName, lastName, email
                )
            } returns defaultStudentSimpleDTO

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
                service.createStudent(
                    firstName, lastName, email
                )
            }
        }
    }
}
