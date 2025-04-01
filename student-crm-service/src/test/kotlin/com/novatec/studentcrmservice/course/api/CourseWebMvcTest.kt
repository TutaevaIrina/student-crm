package com.novatec.studentcrmservice.course.api

import com.ninjasquad.springmockk.MockkBean
import com.novatec.studentcrmservice.course.TestDataProvider.courseNameIT
import com.novatec.studentcrmservice.course.TestDataProvider.courseNameMathe
import com.novatec.studentcrmservice.course.TestDataProvider.defaultCourseDTO
import com.novatec.studentcrmservice.course.TestDataProvider.defaultCourseId
import com.novatec.studentcrmservice.course.TestDataProvider.defaultSimpleCourseDTO
import com.novatec.studentcrmservice.course.TestDataProvider.jsonPropertyIsBlank
import com.novatec.studentcrmservice.course.TestDataProvider.jsonPropertyIsMissing
import com.novatec.studentcrmservice.course.TestDataProvider.jsonPropertyIsNotValid
import com.novatec.studentcrmservice.course.business.CourseService
import com.novatec.studentcrmservice.shared.errorhandling.CourseNotFoundException
import com.novatec.studentcrmservice.shared.errorhandling.CoursenameIsAlreadyExistException
import com.novatec.studentcrmservice.student.api.prettyDocument
import com.novatec.studentcrmservice.utils.ObjectMapper.objectMapper
import io.mockk.every
import io.mockk.verify
import org.hamcrest.core.StringContains.containsString
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(CourseController::class)
@MockkBean(CourseService::class)
@AutoConfigureRestDocs(uriPort = 8080)
class CourseWebMvcTest @Autowired constructor(
    private val courseService: CourseService,
    private val mockMvc: MockMvc
) {

    @Nested
    inner class GetAllCourses {

        @Test
        fun `getting courses returns response 200 OK`() {
            every { courseService.getAllCourses() } returns listOf(
                defaultCourseDTO.copy(id = 1L),
                defaultCourseDTO.copy(id = 2L, courseNameIT)
            )

            mockMvc.perform(get("/course-api/courses"))
                .andExpectAll(
                    status().isOk,
                    content().contentType(APPLICATION_JSON),
                    content().json(
                        objectMapper(
                            listOf(
                                defaultCourseDTO.copy(id = 1L),
                                defaultCourseDTO.copy(id = 2L, courseNameIT)
                            )
                        )
                    )
                ).andDo(prettyDocument("get-all-courses"))

            verify { courseService.getAllCourses() }
        }

        @Test
        fun `getting courses returns 400 BAD REQUEST when request method is wrong`() {
            mockMvc.perform(post("/course-api/courses"))
                .andExpectAll(
                    status().isBadRequest,
                    content().string(containsString("Request method 'POST' is not supported"))
                )
        }

        @Test
        fun `getting courses returns 500 INTERNAL SERVER ERROR when url is wrong`() {
            mockMvc.perform(get("/course-api/cour"))
                .andExpectAll(
                    status().isInternalServerError,
                    content().string(containsString("An internal error occurred"))
                )
        }
    }

    @Nested
    inner class GetCourseById {

        @Test
        fun `getting course by id`() {
            every { courseService.getCourseById(defaultCourseId) } returns defaultCourseDTO

            mockMvc.perform(get("/course-api/course/{courseId}", defaultCourseId))
                .andExpectAll(
                    status().isOk,
                    content().contentType(APPLICATION_JSON),
                    content().json(objectMapper(defaultCourseDTO))
                ).andDo(prettyDocument("get-course-with-id"))

            verify { courseService.getCourseById(defaultCourseId) }
        }

        @Test
        fun `getting course by id returns response 404 NOT FOUND when course was not found`() {
            every { courseService.getCourseById(defaultCourseId) } throws CourseNotFoundException()

            mockMvc.perform(get("/course-api/course/{courseId}", defaultCourseId))
                .andExpectAll(
                    status().isNotFound,
                    content().string(containsString(CourseNotFoundException().message))
                )
        }

        @Test
        fun `getting course by id returns 400 response when request methode is wrong`() {
            mockMvc.perform(post("/course-api/course/{courseId}", defaultCourseId))
                .andExpectAll(
                    status().isBadRequest,
                    content().string(containsString("Request method 'POST' is not supported"))
                )
        }

        @Test
        fun `getting course by id returns 500 response when url is wrong`() {
            mockMvc.perform(get("/course-api/cose/{courseId}", defaultCourseId))
                .andExpectAll(
                    status().isInternalServerError,
                    content().string(containsString("An internal error occurred"))
                )
        }
    }

    @Nested
    inner class CreateNewCourseDTO {

        @Test
        fun `creating a course returns 201 response`() {
            every { courseService.createNewCourse(courseNameMathe) } returns defaultSimpleCourseDTO

            mockMvc.perform(
                post("/course-api/course")
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper(defaultSimpleCourseDTO))
            )
                .andExpectAll(
                    status().isCreated,
                    content().contentType(APPLICATION_JSON),
                    content().json(objectMapper(defaultSimpleCourseDTO))
                ).andDo(
                    prettyDocument("create-course")
                )

            verify { courseService.createNewCourse(courseNameMathe) }
        }

        @Test
        fun `creating a course returns 400 response when the JSON property name is not correct or missing`() {
            mockMvc.perform(
                post("/course-api/course")
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(jsonPropertyIsMissing)
            ).andExpectAll(
                status().isBadRequest,
                content().string(containsString("value failed for JSON property courseName due to missing (therefore NULL) value for creator parameter courseName which is a non-nullable type"))
            )
        }

        @Test
        fun `creating a course returns 400 response when the JSON property name cannot be blank`() {
            mockMvc.perform(
                post("/course-api/course")
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(jsonPropertyIsBlank)
            ).andExpectAll(
                status().isBadRequest,
                content().string(containsString("The Field [courseName] can not be blank."))
            )
        }

        @Test
        fun `creating a course returns 400 response when the JSON property name is not valid`() {
            mockMvc.perform(
                post("/course-api/course")
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(jsonPropertyIsNotValid)
            ).andExpectAll(
                status().isBadRequest,
                content().string(containsString("The value [\$] is not a valid [courseName] because pattern [([a-zA-Z0-9_-]{2,25})( [a-zA-Z0-9]{2,25})*] is required."))
            )
        }

        @Test
        fun `creating a course returns 409 response when course is already exits`() {
            every { courseService.createNewCourse(courseNameMathe) } throws CoursenameIsAlreadyExistException()

            mockMvc.perform(
                post("/course-api/course")
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper(defaultSimpleCourseDTO))
            ).andExpectAll(
                status().isConflict,
                content().string(containsString("The enter coursename is already taken."))
            )
        }

        @Test
        fun `creating course returns 400 response when the request method is wrong`() {
            mockMvc.perform(get("/course-api/course"))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `creating a course returns 500 when the path url is not correct`() {
            mockMvc.perform(post("/course-api/cour"))
                .andExpect(status().isInternalServerError)
        }
    }
}
