package com.novatec.studentcrmservice.course

import com.novatec.studentcrmservice.utils.InitializeWithContainerizedPostgreSQL
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType.JSON
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@InitializeWithContainerizedPostgreSQL
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CourseSpringBootTest {

    @BeforeEach
    fun setupForSpecification(@LocalServerPort port: Int) {
        RestAssured.requestSpecification = RequestSpecBuilder()
            .apply {
                setBaseUri("http://localhost:$port")
                setContentType(JSON)
            }.build()
    }

    @Test
    fun getAllCourses() {
        When {
            get("/course-api/courses")
        } Then { statusCode(200) }
    }

    @Test
    fun createsCourse() {
        Given {
            body(
                """ 
                    {
                        "courseName": "Mathe"
                    }
                """
            )
        } When {
            post("/course-api/course")
        } Then { statusCode(201) }
    }
}
