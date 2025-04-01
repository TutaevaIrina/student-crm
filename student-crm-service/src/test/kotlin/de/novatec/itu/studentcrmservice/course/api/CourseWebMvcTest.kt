package de.novatec.itu.studentcrmservice.course.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseDTO
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseId
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseNameEnglish
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseNameSpanish
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseRequestBody
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseSimpleDTO
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseWithStudentIdsDTO
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultStudentId
import de.novatec.itu.studentcrmservice.course.TestDataProvider.jsonPropertyIsBlank
import de.novatec.itu.studentcrmservice.course.TestDataProvider.jsonPropertyIsInvalid
import de.novatec.itu.studentcrmservice.course.TestDataProvider.jsonPropertyIsMissing
import de.novatec.itu.studentcrmservice.course.business.CourseService
import de.novatec.itu.studentcrmservice.exceptions.*
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.pathParameters

@WebMvcTest(CourseController::class)
@MockkBean(CourseService::class)
@AutoConfigureRestDocs(uriPort = 8080)
class CourseWebMvcTest @Autowired constructor(
    private val mvc: MockMvc,
    private val courseService: CourseService,
) {
    @Nested
    inner class GetAllCourses {
        @Test
        fun `getting courses returns response 200 OK`() {
            every { courseService.findAllCourses() } returns listOf(
                defaultCourseWithStudentIdsDTO,
                defaultCourseWithStudentIdsDTO.copy(id = 2L, courseName = defaultCourseNameSpanish)
            )

            mvc.perform(get("/course-api/courses"))
                .andExpectAll(
                    status().isOk,
                    content().json(
                        jacksonObjectMapper().writeValueAsString(
                            listOf(
                                defaultCourseSimpleDTO,
                                defaultCourseSimpleDTO.copy(id = 2L, courseName = defaultCourseNameSpanish)
                            )
                        )
                    )
                ).andDo(
                    prettyDocument(
                        "get-all-courses",
                        responseFields(getCoursesResponse)
                    )
                )
            verify { courseService.findAllCourses() }
        }

        @Test
        fun `getting courses returns response 500 INTERNAL SERVER ERROR when path is invalid`() {
            mvc.perform(
                get("/course-api/cour")
            )
                .andExpectAll(
                    status().isInternalServerError,
                    content().string(containsString("An internal error occurred"))
                )
        }
    }

    @Nested
    inner class CreateCourse {
        @Test
        fun `creating new course returns response 201 CREATED`() {
            every { courseService.createCourse(defaultCourseNameEnglish) } returns defaultCourseSimpleDTO

            mvc.perform(
                post("/course-api/course")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jacksonObjectMapper().writeValueAsString(defaultCourseRequestBody))
            )
                .andExpectAll(
                    status().isCreated,
                    content().contentType(MediaType.APPLICATION_JSON),
                    content().json(jacksonObjectMapper().writeValueAsString(defaultCourseSimpleDTO))
                )
                .andDo(
                    prettyDocument(
                        "create-course",
                        requestFields(courseRequest),
                        responseFields(courseResponse)
                    )
                )

            verify { courseService.createCourse(defaultCourseNameEnglish) }
        }

        @Test
        fun `creating new course returns response 409 CONFLICT when course name already exists`() {
            every { courseService.createCourse(defaultCourseNameEnglish) } throws CourseAlreadyExistsException(
                defaultCourseNameEnglish
            )

            mvc.perform(
                post("/course-api/course")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jacksonObjectMapper().writeValueAsString(defaultCourseRequestBody))
            )
                .andExpectAll(
                    status().isConflict,
                    content().string(containsString(CourseAlreadyExistsException(defaultCourseNameEnglish).message))
                )
        }

        @Test
        fun `creating new course returns response 400 BAD REQUEST when the input is blank`() {
            mvc.perform(
                post("/course-api/course")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonPropertyIsBlank)
            )
                .andExpectAll(
                    status().isBadRequest,
                    content().string(containsString("Course name should not be blank or it does not match pattern ^[a-zA-Z0-9_-]{2,25}( [a-zA-Z0-9]{2,25})*$"))
                )
        }

        @Test
        fun `creating new course returns response 400 BAD REQUEST when the input does not match the pattern`() {
            mvc.perform(
                post("/course-api/course")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonPropertyIsInvalid)
            )
                .andExpectAll(
                    status().isBadRequest,
                    content().string(containsString("Course name should not be blank or it does not match pattern ^[a-zA-Z0-9_-]{2,25}( [a-zA-Z0-9]{2,25})*$"))
                )
        }

        @Test
        fun `creating new course returns response 400 BAD REQUEST when the body is invalid`() {
            mvc.perform(
                post("/course-api/course")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonPropertyIsMissing)
            )
                .andExpectAll(
                    status().isBadRequest,
                    content().string(containsString("The body contains invalid values"))
                )
        }

        @Test
        fun `creating new course returns 500 INTERNAL SERVER ERROR when path is invalid`() {
            mvc.perform(
                post("/course-api/cou")
            )
                .andExpectAll(
                    status().isInternalServerError,
                    content().string(containsString("An internal error occurred"))
                )
        }
    }

    @Nested
    inner class GetCourseById {
        @Test
        fun `getting course by id returns response 200 OK`() {
            every { courseService.findCourseById(defaultCourseId) } returns defaultCourseDTO

            mvc.perform(get("/course-api/course/{courseId}", defaultCourseId))
                .andExpectAll(
                    status().isOk,
                    content().json(jacksonObjectMapper().writeValueAsString(defaultCourseDTO))
                )
                .andDo(
                    prettyDocument(
                        "get-course-by-id",
                        pathParameters(courseIdParam),
                        responseFields(getCourseByIdResponse)
                    )
                )

            verify { courseService.findCourseById(defaultCourseId) }
        }

        @Test
        fun `getting course by id returns response 404 NOT FOUND when course does not exist by the given id`() {
            every { courseService.findCourseById(defaultCourseId) } throws CourseNotFoundException(defaultCourseId)

            mvc.perform(get("/course-api/course/{courseId}", defaultCourseId))
                .andExpectAll(
                    status().isNotFound,
                    content().string(containsString(CourseNotFoundException(defaultCourseId).message))
                )
        }

        @Test
        fun `getting course by id returns response 500 INTERNAL SERVER ERROR when path is invalid`() {
            mvc.perform(
                get("/course-api/cou/{courseId}", defaultCourseId)
            )
                .andExpectAll(
                    status().isInternalServerError,
                    content().string(containsString("An internal error occurred"))
                )
        }
    }

    @Nested
    inner class GetCourseByName {
        @Test
        fun `getting courses by name returns response 200 OK`() {
            every { courseService.findCourseByName(defaultCourseNameEnglish.substring(4)) } returns listOf(
                defaultCourseSimpleDTO,
                defaultCourseSimpleDTO.copy(id = 2L, courseName = "Spanish")
            )

            mvc.perform(get("/course-api/courses/{name}", defaultCourseNameEnglish.substring(4)))
                .andExpectAll(
                    status().isOk,
                    content().json(
                        jacksonObjectMapper().writeValueAsString(
                            listOf(
                                defaultCourseSimpleDTO,
                                defaultCourseSimpleDTO.copy(id = 2L, courseName = "Spanish")
                            )
                        )
                    )
                ).andDo(
                    prettyDocument(
                        "get-course-by-name",
                        pathParameters(courseNameParam),
                        responseFields(getCourseByNameResponse)
                    )
                )

            verify { courseService.findCourseByName(defaultCourseNameEnglish.substring(4)) }
        }

        @Test
        fun `getting courses by name returns response 404 NOT FOUND when course does not exist by the given name`() {
            every { courseService.findCourseByName(defaultCourseNameEnglish) } throws CourseNotFoundException(
                defaultCourseNameEnglish
            )

            mvc.perform(get("/course-api/courses/{name}", defaultCourseNameEnglish))
                .andExpectAll(
                    status().isNotFound,
                    content().string(containsString(CourseNotFoundException(defaultCourseNameEnglish).message))
                )
        }

        @Test
        fun `getting courses by name returns response 500 INTERNAL SERVER ERROR when the path is invalid`() {
            mvc.perform(
                get("/course-api/cour/{name}", defaultCourseNameEnglish)
            )
                .andExpectAll(
                    status().isInternalServerError,
                    content().string(containsString("An internal error occurred"))
                )
        }
    }

    @Nested
    inner class UpdateCourse {
        @Test
        fun `updating course returns response 200 OK`() {
            every {
                courseService.updateCourse(
                    defaultCourseId,
                    defaultCourseNameSpanish
                )
            } returns defaultCourseSimpleDTO.copy(id = 1L, courseName = defaultCourseNameSpanish)
            mvc.perform(
                put("/course-api/course/{courseId}", defaultCourseId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jacksonObjectMapper().writeValueAsString(defaultCourseRequestBody.copy(courseName = defaultCourseNameSpanish)))
            )
                .andExpectAll(
                    status().isOk,
                    content().json(
                        jacksonObjectMapper().writeValueAsString(
                            defaultCourseSimpleDTO.copy(
                                id = 1L,
                                courseName = defaultCourseNameSpanish
                            )
                        )
                    )
                ).andDo(
                    prettyDocument(
                        "update-course",
                        pathParameters(courseIdParam),
                        requestFields(courseRequest),
                        responseFields(courseResponse)
                    )
                )

            verify { courseService.updateCourse(defaultCourseId, courseName = defaultCourseNameSpanish) }
        }


        @Test
        fun `updating course returns response 404 NOT FOUND when course does not exist by the given id`() {
            every {
                courseService.updateCourse(
                    defaultCourseId,
                    defaultCourseNameEnglish
                )
            } throws CourseNotFoundException(
                defaultCourseId
            )

            mvc.perform(
                put("/course-api/course/{courseId}", defaultCourseId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jacksonObjectMapper().writeValueAsString(defaultCourseRequestBody))
            )
                .andExpectAll(
                    status().isNotFound,
                    content().string(containsString(CourseNotFoundException(defaultCourseId).message))
                )
        }

        @Test
        fun `updating course returns response 400 BAD REQUEST when the input is blank`() {
            mvc.perform(
                put("/course-api/course/{courseId}", defaultCourseId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jacksonObjectMapper().writeValueAsString(defaultCourseRequestBody.copy(courseName = "")))
            )
                .andExpectAll(
                    status().isBadRequest,
                    content().string(containsString("Course name should not be blank or it does not match pattern ^[a-zA-Z0-9_-]{2,25}( [a-zA-Z0-9]{2,25})*$"))
                )
        }

        @Test
        fun `updating course returns response 400 BAD REQUEST when the body is invalid`() {
            mvc.perform(
                put("/course-api/course/{courseId}", defaultCourseId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonPropertyIsMissing)
            )
                .andExpect(status().isBadRequest)
                .andExpect(content().string(containsString("The body contains invalid values")))
        }

        @Test
        fun `updating course returns response 500 ENTERNAL SERVER ERROR when path is invalid`() {
            mvc.perform(
                put("/course-api/cour/{courseId}", defaultCourseId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jacksonObjectMapper().writeValueAsString(defaultCourseRequestBody.copy(courseName = "Spanish")))
            )
                .andExpectAll(
                    status().isInternalServerError,
                    content().string(containsString("An internal error occurred"))
                )
        }
    }

    @Nested
    inner class DeleteCourseById {
        @Test
        fun `deleting course returns response 204 NO CONTENT`() {
            every { courseService.deleteCourse(defaultCourseId) } just Runs

            mvc.perform(delete("/course-api/course/{courseId}", defaultCourseId))
                .andExpect(status().isNoContent)
                .andDo(
                    prettyDocument(
                        "delete-course-by-id",
                        pathParameters(courseIdParam)
                    )
                )
            verify { courseService.deleteCourse(defaultCourseId) }
        }

        @Test
        fun `deleting course returns response 404 NOT FOUND when when course does not exist by the given id`() {
            every { courseService.deleteCourse(defaultCourseId) } throws CourseNotFoundException(defaultCourseId)

            mvc.perform(delete("/course-api/course/{courseId}", defaultCourseId))
                .andExpectAll(
                    status().isNotFound,
                    content().string(containsString(CourseNotFoundException(defaultCourseId).message))
                )
        }

        @Test
        fun `deleting course returns response 500 INTERNAL SERVER ERROR when the path is invalid`() {
            mvc.perform(
                delete("/course-api/cour/{courseId}", defaultCourseId)
            )
                .andExpectAll(
                    status().isInternalServerError,
                    content().string(containsString("An internal error occurred"))
                )
        }
    }

    @Nested
    inner class AddStudentToCourse {
        @Test
        fun `adding student to course returns response 204 NO CONTENT`() {
            every { courseService.addStudentToCourse(defaultCourseId, defaultStudentId) } just Runs
            mvc.perform(put("/course-api/course/{courseId}/student/{studentId}", defaultCourseId, defaultStudentId))
                .andExpect(status().isNoContent)
                .andDo(
                    prettyDocument(
                        "add-student-to-course",
                        pathParameters(courseIdParam, studentIdParam)
                    )
                )
            verify { courseService.addStudentToCourse(defaultCourseId, defaultStudentId) }
        }

        @Test
        fun `adding student to course returns response 409 CONFLICT when student is already enrolled`() {
            every {
                courseService.addStudentToCourse(
                    defaultCourseId,
                    defaultStudentId
                )
            } throws StudentAlreadyExistsException(defaultCourseId, defaultStudentId)
            mvc.perform(put("/course-api/course/{courseId}/student/{studentId}", defaultCourseId, defaultStudentId))
                .andExpectAll(
                    status().isConflict,
                    content().string(
                        containsString(
                            StudentAlreadyExistsException(
                                defaultCourseId,
                                defaultStudentId
                            ).message
                        )
                    )
                )
        }

        @Test
        fun `adding student to course returns response 404 NOT FOUND when course does not exist`() {
            every {
                courseService.addStudentToCourse(
                    defaultCourseId,
                    defaultStudentId
                )
            } throws CourseNotFoundException(defaultCourseId)
            mvc.perform(put("/course-api/course/{courseId}/student/{studentId}", defaultCourseId, defaultStudentId))
                .andExpectAll(
                    status().isNotFound,
                    content().string(containsString(CourseNotFoundException(defaultCourseId).message))
                )
        }

        @Test
        fun `adding student to course returns response 404 NOT FOUND when student does not exist`() {
            every {
                courseService.addStudentToCourse(
                    defaultCourseId,
                    defaultStudentId
                )
            } throws StudentNotFoundException(defaultStudentId)
            mvc.perform(
                put("/course-api/course/{courseId}/student/{studentId}", defaultCourseId, defaultStudentId)
            )
                .andExpectAll(
                    status().isNotFound,
                    content().string(containsString(StudentNotFoundException(defaultStudentId).message))
                )
        }

        @Test
        fun `adding student to course returns response 500 INTERNAL SERVER ERROR when the path is invalid`() {
            mvc.perform(
                put("/course-api/cour/{courseId}/student/{studentId}", defaultCourseId, defaultStudentId)
            )
                .andExpectAll(
                    status().isInternalServerError,
                    content().string(containsString("An internal error occurred"))
                )
        }
    }

    @Nested
    inner class RemoveStudentFromCourse {
        @Test
        fun `removing student from course returns response 204 NO CONTENT`() {
            every { courseService.removeStudentFromCourse(defaultCourseId, defaultStudentId) } just Runs

            mvc.perform(delete("/course-api/course/{courseId}/student/{studentId}", defaultCourseId, defaultStudentId))
                .andExpect(status().isNoContent)
                .andDo(
                    prettyDocument(
                        "remove-student-from-course",
                        pathParameters(courseIdParam, studentIdParam)
                    )
                )

            verify { courseService.removeStudentFromCourse(defaultCourseId, defaultStudentId) }
        }

        @Test
        fun `removing student from course returns response 404 NOT FOUND when course does not exist`() {
            every {
                courseService.removeStudentFromCourse(
                    defaultCourseId,
                    defaultStudentId
                )
            } throws CourseNotFoundException(
                defaultStudentId
            )

            mvc.perform(delete("/course-api/course/{courseId}/student/{studentId}", defaultCourseId, defaultStudentId))
                .andExpectAll(
                    status().isNotFound,
                    content().string(containsString(CourseNotFoundException(defaultCourseId).message))
                )
        }

        @Test
        fun `removing student from course returns response 404 NOT FOUND when student does not exist`() {
            every {
                courseService.removeStudentFromCourse(
                    defaultCourseId,
                    defaultStudentId
                )
            } throws StudentNotFoundException(
                defaultStudentId
            )

            mvc.perform(
                delete("/course-api/course/{courseId}/student/{studentId}", defaultCourseId, defaultStudentId)
            )
                .andExpectAll(
                    status().isNotFound,
                    content().string(containsString(StudentNotFoundException(defaultStudentId).message))
                )
        }

        @Test
        fun `removing student from course returns response 409 CONFLICT when student is not enrolled in the course`() {
            every {
                courseService.removeStudentFromCourse(
                    defaultCourseId,
                    defaultStudentId
                )
            } throws StudentNotEnrolledInCourseException(
                defaultCourseId,
                defaultStudentId
            )
            mvc.perform(
                delete("/course-api/course/{courseId}/student/{studentId}", defaultCourseId, defaultStudentId)
            )
                .andExpectAll(
                    status().isConflict,
                    content().string(
                        containsString(
                            StudentNotEnrolledInCourseException(
                                defaultCourseId,
                                defaultStudentId
                            ).message
                        )
                    )
                )
        }

        @Test
        fun `removing student from course returns response 500 INTERNAL SERVER ERROR when the path is invalid`() {
            mvc.perform(
                delete("/course-api/cour/{courseId}/student/{studentId}", defaultCourseId, defaultStudentId)
            )
                .andExpectAll(
                    status().isInternalServerError,
                    content().string(containsString("An internal error occurred"))
                )
        }
    }
}
