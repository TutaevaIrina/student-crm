package de.novatec.itu.studentcrmservice.course.api

import com.ninjasquad.springmockk.MockkBean
import de.novatec.itu.studentcrmservice.course.TestDataProvider.courseNameIT
import de.novatec.itu.studentcrmservice.course.TestDataProvider.courseNameMathe
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseDTO
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseId
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultSimpleCourseDTO
import de.novatec.itu.studentcrmservice.course.TestDataProvider.jsonValueIsBlank
import de.novatec.itu.studentcrmservice.course.TestDataProvider.jsonPropertyIsWrong
import de.novatec.itu.studentcrmservice.course.TestDataProvider.jsonValueIsNotValid
import de.novatec.itu.studentcrmservice.course.business.CourseService
import de.novatec.itu.studentcrmservice.exceptions.CourseNotFoundException
import de.novatec.itu.studentcrmservice.exceptions.CourseAlreadyExistsException
import de.novatec.itu.studentcrmservice.course.TestDataProvider.courseWithStudentIdsDTO1
import de.novatec.itu.studentcrmservice.course.TestDataProvider.courseWithStudentIdsDTO2
import de.novatec.itu.studentcrmservice.student.api.prettyDocument
import de.novatec.itu.studentcrmservice.utils.ObjectMapper.objectMapper
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
        fun `getting courses returns 200 OK`() {
            every { courseService.findAllCourses() } returns listOf(
                courseWithStudentIdsDTO1,
                courseWithStudentIdsDTO2
            )

            mockMvc.perform(get("/course-api/courses"))
                .andExpectAll(
                    status().isOk,
                    content().contentType(APPLICATION_JSON),
                    content().json(
                        objectMapper(
                            listOf(
                                defaultCourseDTO,
                                defaultCourseDTO.copy(id = 1L, courseNameIT)
                            )
                        )
                    )
                ).andDo(prettyDocument("get-all-courses"))

            verify { courseService.findAllCourses() }
        }

        @Test
        fun `getting courses returns 500 INTERNAL SERVER ERROR when wrong HTTP method is used`() {
            mockMvc.perform(post("/course-api/courses"))
                .andExpectAll(
                    status().isInternalServerError,
                    content().string(containsString("An internal error occurred"))
                )
        }

        @Test
        fun `getting courses returns 500 INTERNAL SERVER ERROR when wrong URL is used`() {
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
        fun `getting a course by id`() {
            every { courseService.getCourseById(defaultCourseId) } returns defaultCourseDTO

            mockMvc.perform(get("/course-api/course/{courseId}", defaultCourseId))
                .andExpectAll(
                    status().isOk,
                    content().contentType(APPLICATION_JSON),
                    content().json(objectMapper(defaultCourseDTO))
                ).andDo(prettyDocument("get-course-by-id"))

            verify { courseService.getCourseById(defaultCourseId) }
        }

        @Test
        fun `getting a course by id returns 404 NOT FOUND when course was not found`() {
            every { courseService.getCourseById(defaultCourseId) } throws CourseNotFoundException(defaultCourseId)

            mockMvc.perform(get("/course-api/course/{courseId}", defaultCourseId))
                .andExpectAll(
                    status().isNotFound,
                    content().string(containsString(CourseNotFoundException(defaultCourseId).message))
                )
        }

        @Test
        fun `getting a course by id returns 500 INTERNAL SERVER ERROR when request methode is wrong`() {
            mockMvc.perform(post("/course-api/course/{courseId}", defaultCourseId))
                .andExpectAll(
                    status().isInternalServerError,
                    content().string(containsString("An internal error occurred"))
                )
        }

        @Test
        fun `getting a course by id returns 500 INTERNAL SERVER ERROR when url is wrong`() {
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
        fun `creating a course returns 201 CREATED`() {
            every { courseService.createCourse(courseNameMathe) } returns defaultSimpleCourseDTO

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

            verify { courseService.createCourse(courseNameMathe) }
        }

        @Test
        fun `creating a course returns 400 BAD REQUEST when the JSON property is wrong or missing`() {
            mockMvc.perform(
                post("/course-api/course")
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(jsonPropertyIsWrong)
            ).andExpectAll(
                status().isBadRequest,
                content().string(containsString("The body contains invalid values"))
            )
        }

        @Test
        fun `creating a course returns 400 BAD REQUEST when the JSON value is blank`() {
            mockMvc.perform(
                post("/course-api/course")
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(jsonValueIsBlank)
            ).andExpectAll(
                status().isBadRequest,
                content().string(containsString("Course name should not be blank or it does not match pattern ^[a-zA-Z0-9_-]{2,25}( [a-zA-Z0-9]{2,25})*\$"))
            )
        }

        @Test
        fun `creating a course returns 400 BAD REQUEST when the JSON value is not valid`() {
            mockMvc.perform(
                post("/course-api/course")
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(jsonValueIsNotValid)
            ).andExpectAll(
                status().isBadRequest,
                content().string(containsString("Course name should not be blank or it does not match pattern ^[a-zA-Z0-9_-]{2,25}( [a-zA-Z0-9]{2,25})*\$"))
            )
        }


        @Test
        fun `creating a course returns 409 CONFLICT when course is already exist`() {
            every { courseService.createCourse(courseNameMathe) } throws CourseAlreadyExistsException(courseNameMathe)

            mockMvc.perform(
                post("/course-api/course")
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper(defaultSimpleCourseDTO))
            ).andExpectAll(
                status().isConflict,
                content().string(containsString("Course with name $courseNameMathe already exists"))
            )
        }

        @Test
        fun `creating a course returns 500 INTERNAL SERVER ERROR when wrong HTTP method is used`() {
            mockMvc.perform(get("/course-api/course"))
                .andExpectAll(
                    status().isInternalServerError,
                    content().string(containsString("An internal error occurred"))
                )
        }

        @Test
        fun `creating a course returns 500 INTERNAL SERVER ERROR when wrong URL is used`() {
            mockMvc.perform(post("/course-api/cour"))
                .andExpectAll(
                    status().isInternalServerError,
                    content().string(containsString("An internal error occurred"))
                )
        }
    }
}
