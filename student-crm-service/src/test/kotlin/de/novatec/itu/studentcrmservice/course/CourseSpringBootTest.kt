package de.novatec.itu.studentcrmservice.course

import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseDTO
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseEntityList
import de.novatec.itu.studentcrmservice.course.api.CourseRequestBodyDTO
import de.novatec.itu.studentcrmservice.course.model.CourseSimpleDTO
import de.novatec.itu.studentcrmservice.course.persistence.CourseEntity
import de.novatec.itu.studentcrmservice.course.persistence.CourseRepository
import de.novatec.itu.studentcrmservice.student.persistence.StudentEntity
import de.novatec.itu.studentcrmservice.student.persistence.StudentRepository
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType.JSON
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test", "docker")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CourseSpringBootTest(
    @Autowired private val courseRepository: CourseRepository,
    @Autowired private val studentRepository: StudentRepository
) {
    private val courseEntity = CourseEntity(id = 1L, courseName = "Spanish")
    private val studentEntity = StudentEntity(id = 1L, firstName = "Max", lastName = "Smith", email = "max@smith.de")

    @BeforeEach
    fun setUpForSpecification(@LocalServerPort port: Int) {
        RestAssured.requestSpecification = RequestSpecBuilder()
            .apply {
                setBaseUri("http://localhost:$port")
                setContentType(JSON)
            }.build()
    }

    @AfterEach
    fun tearDown() {
        courseRepository.deleteAll()
        studentRepository.deleteAll()
    }

    @Test
    fun `should return all courses`() {
        courseRepository.findAll() shouldBe emptyList()

        courseRepository.saveAll(defaultCourseEntityList)

        When {
            get("/course-api/courses")
        } Then {
            statusCode(200)
            body("size()", equalTo(defaultCourseEntityList.size))
            body("[0].courseName", equalTo(defaultCourseEntityList[0].courseName))
            body("[1].courseName", equalTo(defaultCourseEntityList[1].courseName))
        }
    }

    @Test
    fun `should create a course`() {
        val defaultCourseRequestBody = CourseRequestBodyDTO(courseName = "English")

        courseRepository.existsByCourseNameIgnoreCase(defaultCourseRequestBody.courseName) shouldBe false

        val addedCourse =
            Given {
                contentType("application/json")
                body(defaultCourseRequestBody)
            }
                .When {
                    post("/course-api/course")
                } Then {
                statusCode(201)
            } Extract {
                `as`(CourseSimpleDTO::class.java)
            }

        addedCourse.courseName shouldBe defaultCourseRequestBody.courseName
        courseRepository.existsByCourseNameIgnoreCase(defaultCourseRequestBody.courseName) shouldBe true
    }

    @Test
    fun `should return a course by id`() {
        courseRepository.findAll() shouldBe emptyList()

        val courseSaved = courseRepository.save(courseEntity)

        When {
            get("/course-api/course/{courseId}", courseSaved.id)
        } Then {
            statusCode(200)
            body("courseName", equalTo(courseEntity.courseName))
        }
    }

    @Test
    fun `should return list of courses by name`() {
        courseRepository.findAll() shouldBe emptyList()

        courseRepository.saveAll(defaultCourseEntityList)

        When {
            get("/course-api/courses/{name}", "ish")
        } Then {
            statusCode(200)
            body("size()", equalTo(defaultCourseEntityList.size))
            body("[0].courseName", equalTo(defaultCourseEntityList[0].courseName))
            body("[1].courseName", equalTo(defaultCourseEntityList[1].courseName))
        }
    }

    @Test
    fun `should update a course by id`() {
        courseRepository.findAll() shouldBe emptyList()

        val savedCourse = courseRepository.save(courseEntity)

        val updatedCourse =
            Given {
                contentType("application/json")
                body(defaultCourseDTO)
            }
                .When {
                    put("/course-api/course/{courseId}", savedCourse.id)
                } Then {
                statusCode(200)
            } Extract {
                `as`(CourseSimpleDTO::class.java)
            }

        updatedCourse.courseName shouldBe defaultCourseDTO.courseName
        courseRepository.existsByCourseNameIgnoreCase(defaultCourseDTO.courseName) shouldBe true
    }

    @Test
    fun `should delete a courses by id`() {
        courseRepository.findAll() shouldBe emptyList()

        val savedCourse = courseRepository.save(courseEntity)

        When {
            delete("/course-api/course/{courseId}", savedCourse.id)
        } Then {
            statusCode(204)
        }

        courseRepository.existsByCourseNameIgnoreCase(savedCourse.courseName) shouldBe false
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    fun `should add a student to a course by id`() {
        courseRepository.findAll() shouldBe emptyList()
        studentRepository.findAll() shouldBe emptyList()

        val savedCourse = courseRepository.save(courseEntity)
        val savedStudent = studentRepository.save(studentEntity)

        When {
            put("/course-api/course/{courseId}/student/{studentId}", savedCourse.id, savedStudent.id)
        } Then {
            statusCode(204)
        }
    }

    @Test
    fun `should remove a student from a course by id`() {
        courseRepository.findAll() shouldBe emptyList()
        studentRepository.findAll() shouldBe emptyList()

        val savedCourse = courseRepository.save(courseEntity)
        val savedStudent = studentRepository.save(studentEntity)

        When {
            put("/course-api/course/{courseId}/student/{studentId}", savedCourse.id, savedStudent.id)
        } Then {
            statusCode(204)
        }

        When {
            delete("/course-api/course/{courseId}/student/{studentId}", savedCourse.id, savedStudent.id)
        } Then {
            statusCode(204)
        }
    }
}
