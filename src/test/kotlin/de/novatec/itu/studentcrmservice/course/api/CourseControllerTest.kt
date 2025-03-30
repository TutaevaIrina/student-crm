package de.novatec.itu.studentcrmservice.course.api

import de.novatec.itu.studentcrmservice.course.business.CourseService
import org.junit.jupiter.api.Test

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import com.ninjasquad.springmockk.MockkBean
import de.novatec.itu.studentcrmservice.course.persistence.CourseEntity
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(CourseController::class)
@MockkBean(classes = [CourseService::class])
class CourseControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val courseService: CourseService,
){

    private val courseEntity1 = CourseEntity(
        id = 1,
        courseName = "English",
        students = mutableSetOf()
    )

    private val courseEntity2 = CourseEntity(
        id = 2,
        courseName = "Spanish",
        students = mutableSetOf()
    )

    private val courseEntityList = listOf(courseEntity1, courseEntity2)

    @Nested
    inner class GetAllCourses {
        @Test
        fun `should return all courses`() {
            every { courseService.findAllCourses() } returns courseEntityList.map { it.toDto() }

            mvc.get("/course-api/courses")
                .andExpect {
                    status {
                        isOk()
                    }
                    content {
                        json(
                            """
                                [
                                {
                                "id": 1,
                                "courseName": "English",
                                "students": []
                                },
                                {
                                "id": 2,
                                "courseName": "Spanish",
                                "students": []
                                }
                                ]                               
                            """.trimIndent(),
                            strict = true
                        )
                    }
                }
            verify { courseService.findAllCourses() }
        }
    }

    @Test
    fun createCourse() {
    }

    @Test
    fun getCourseById() {
    }

    @Test
    fun getCourseByName() {
    }

    @Test
    fun updateCourse() {
    }

    @Test
    fun deleteCourseById() {
    }

    @Test
    fun addStudentToCourse() {
    }

    @Test
    fun removeStudentFromCourse() {
    }
}