package de.novatec.itu.studentcrmservice.course.persistence

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test", "docker")
@DataJpaTest
class CourseJPATest(@Autowired private val courseRepository: CourseRepository) {
    private lateinit var courseEntity1: CourseEntity
    private lateinit var courseEntity2: CourseEntity

    @BeforeEach
    fun setUp() {
        courseEntity1 = CourseEntity(courseName = "English")
        courseEntity2 = CourseEntity(courseName = "Spanish")
    }

    @Nested
    inner class FindByNameIgnoreCase {
        @Test
        fun `getting courses by name ignoring case`() {
            courseRepository.save(courseEntity1)
            courseRepository.save(courseEntity2)
            val result = courseRepository.findByNameIgnoreCase("ish")

            assertThat(result).hasSize(2)
            assertThat(result).extracting("courseName").containsOnly("English", "Spanish")
        }

        @Test
        fun `returns empty list when the name does not exist`() {
            val result = courseRepository.findByNameIgnoreCase("ish")

            assertThat(result).isEmpty()
        }
    }

    @Nested
    inner class ExistsByCourseName {
        @Test
        fun `returns true if a course name exists`() {
            courseRepository.save(courseEntity1)

            val result = courseRepository.existsByCourseNameIgnoreCase(courseEntity1.courseName)
            assertThat(result).isTrue
        }

        @Test
        fun `returns false when course name does not exist`() {
            val exists = courseRepository.existsByCourseNameIgnoreCase(courseEntity2.courseName)
            assertThat(exists).isFalse()
        }
    }
}
