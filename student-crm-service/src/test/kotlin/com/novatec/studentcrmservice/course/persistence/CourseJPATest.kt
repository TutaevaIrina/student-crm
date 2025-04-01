package com.novatec.studentcrmservice.course.persistence

import com.novatec.studentcrmservice.course.TestDataProvider.defaultSimpleCourseDTO
import com.novatec.studentcrmservice.course.business.model.toSimpleCourseDto
import com.novatec.studentcrmservice.shared.types.CourseName
import com.novatec.studentcrmservice.utils.InitializeWithContainerizedPostgreSQL
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test", "docker")
@InitializeWithContainerizedPostgreSQL
class CourseJPATest(@Autowired private val repository: CourseRepository) {

    @BeforeEach
    fun init() {
        repository.deleteAll()
    }

    @Test
    fun `find all Courses`() {

        val courseEntity = CourseEntity(courseName = CourseName("Mathe"))

        val courseCreation = repository.save(courseEntity)
        val listOfCourses = repository.findAll()

        listOfCourses shouldBe listOf(courseCreation)
    }

    @Test
    fun `creates a course`() {
        val courseEntity = CourseEntity(courseName = CourseName("Mathe"))
        val expectedData = defaultSimpleCourseDTO.copy(2L)

        val creationOfCourse = repository.save(courseEntity).toSimpleCourseDto()

        creationOfCourse shouldBe expectedData
    }
}
