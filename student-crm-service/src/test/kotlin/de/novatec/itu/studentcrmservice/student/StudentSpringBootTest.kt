package de.novatec.itu.studentcrmservice.student


import de.novatec.itu.studentcrmservice.utils.InitializeWithContainerizedPostgreSQL
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@InitializeWithContainerizedPostgreSQL
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentSpringBootTest {

    @BeforeEach
    fun setupForSpecification(@LocalServerPort port: Int) {
        RestAssured.requestSpecification = RequestSpecBuilder()
            .apply {
                setBaseUri("http://localhost:$port")
                setContentType(ContentType.JSON)
            }.build()
    }

    @Test
    fun getAllStudents() {
        When {
            get("/student-api/students")
        } Then { statusCode(200) }
    }

    @Test
    fun createsStudent() {
        Given {
            body(
                """ 
                    {
                        "firstName": "Maik",
                        "lastName": "Müller",
                        "email": "maikmüller@web.de"
                    }
                """
            )
        } When {
            post("/student-api/student")
        } Then { statusCode(201) }
    }
}
