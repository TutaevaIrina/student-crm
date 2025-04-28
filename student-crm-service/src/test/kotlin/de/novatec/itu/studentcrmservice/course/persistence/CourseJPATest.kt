package de.novatec.itu.studentcrmservice.course.persistence

import de.novatec.itu.studentcrmservice.course.TestDataProvider.courseNameIT
import de.novatec.itu.studentcrmservice.course.TestDataProvider.courseNameMathe
import de.novatec.itu.studentcrmservice.utils.InitializeWithContainerizedPostgreSQL
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test", "docker")
@InitializeWithContainerizedPostgreSQL
class CourseJPATest(@Autowired private val courseRepository: CourseRepository) {

    @BeforeEach
    fun init() {
        courseRepository.deleteAll()
    }

    @Test
    fun `findByNameIgnoreCase returns course`() {
        val course = CourseEntity(courseName = courseNameMathe)
        courseRepository.save(course)

        val result = courseRepository.findByNameIgnoreCase("mat")

        result.isNotEmpty() shouldBe true
        result[0].courseName shouldBe "Mathe"
    }

    @Test
    fun `existsByCourseNameIgnoreCase returns true if course exists`() {
        val course = CourseEntity(courseName = courseNameIT)
        courseRepository.save(course)

        val exists = courseRepository.existsByCourseNameIgnoreCase("IT")

        exists shouldBe true
    }

    @Test
    fun `existsByCourseNameIgnoreCase returns false if course does not exist`() {
        val exists = courseRepository.existsByCourseNameIgnoreCase("Physik")

        exists shouldBe false
    }
}
