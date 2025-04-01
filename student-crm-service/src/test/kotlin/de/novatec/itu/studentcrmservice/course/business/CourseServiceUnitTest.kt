package de.novatec.itu.studentcrmservice.course.business


import assertk.assertThat
import assertk.assertions.isEqualTo
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseEntityList
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseId
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseNameEnglish
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseNameSpanish
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseSimpleDTO
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseWithStudentIdsDTO
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultStudentId
import de.novatec.itu.studentcrmservice.course.persistence.CourseEntity
import de.novatec.itu.studentcrmservice.course.persistence.CourseRepository
import de.novatec.itu.studentcrmservice.exceptions.*
import de.novatec.itu.studentcrmservice.student.persistence.StudentEntity
import de.novatec.itu.studentcrmservice.student.persistence.StudentRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class CourseServiceUnitTest {
    private val courseRepository = mockk<CourseRepository>()
    private val studentRepository = mockk<StudentRepository>()
    private val cut = CourseService(courseRepository, studentRepository)
    private lateinit var studentEntity: StudentEntity
    private lateinit var courseEntity1: CourseEntity
    private lateinit var courseEntity2: CourseEntity


    @BeforeEach
    fun setUp() {
        studentEntity = StudentEntity(id = 1L, "Mark", "Miller", "mark@miller.com", mutableSetOf())
        courseEntity1 = CourseEntity(1L, "English")
        courseEntity2 = CourseEntity(2L, "Spanish", mutableListOf(studentEntity))
    }

    @Nested
    inner class FindAllCourses {
        @Test
        fun `getting courses`() {
            every { courseRepository.findAll() } returns listOf(courseEntity1)

            val result = cut.findAllCourses()

            assertThat(result).isEqualTo(listOf(defaultCourseWithStudentIdsDTO))
            verify { courseRepository.findAll() }
        }

        @Test
        fun `getting empty list of course when no course exists`() {
            every { courseRepository.findAll() } returns emptyList()

            val result = cut.findAllCourses()

            assertThat(result).isEqualTo(emptyList())
            verify { courseRepository.findAll() }
        }
    }

    @Nested
    inner class CreateCourse {
        @Test
        fun `creating a course`() {
            val entityCapture = slot<CourseEntity>()
            every { courseRepository.existsByCourseNameIgnoreCase(defaultCourseNameEnglish) } returns false
            every { courseRepository.save(capture(entityCapture)) } returns courseEntity1

            val result = cut.createCourse(defaultCourseNameEnglish)

            val actualEntity = entityCapture.captured
            assertThat(result).isEqualTo(defaultCourseSimpleDTO)
            assertThat(actualEntity.courseName).isEqualTo(defaultCourseNameEnglish)
            verify { courseRepository.save(actualEntity) }
        }

        @Test
        fun `throws a CourseAlreadyExistsException when the course name exists`() {
            every { courseRepository.existsByCourseNameIgnoreCase(defaultCourseNameEnglish) } returns true

            assertThrows<CourseAlreadyExistsException> {
                cut.createCourse(defaultCourseNameEnglish)
            }

            verify(exactly = 0) { courseRepository.save(any()) }
        }
    }

    @Nested
    inner class FindCourseById {
        @Test
        fun `getting course by id when id exists`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity1)

            val result = cut.findCourseById(defaultCourseId)

            assertThat(result).isEqualTo(courseEntity1.toDto())
            verify { courseRepository.findById(defaultCourseId) }
        }

        @Test
        fun `throws a CourseNotFoundException when id does not exist`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.empty()

            assertThrows<CourseNotFoundException> { cut.findCourseById(defaultCourseId) }

            verify { courseRepository.findById(defaultCourseId) }
        }
    }

    @Nested
    inner class FindCourseByName {
        @Test
        fun `getting all courses with the given name`() {
            every { courseRepository.findByNameIgnoreCase(defaultCourseNameEnglish.substring(4)) } returns defaultCourseEntityList

            val expectedList = defaultCourseEntityList.map { it.toSimpleDTO() }
            val result = cut.findCourseByName(defaultCourseNameEnglish.substring(4))

            assertThat(result).isEqualTo(expectedList)
        }

        @Test
        fun `throws a CourseNotFoundException when no course found by the given name`() {
            every { courseRepository.findByNameIgnoreCase(defaultCourseNameEnglish) } returns emptyList()

            assertThrows<CourseNotFoundException> { cut.findCourseByName(defaultCourseNameEnglish) }
        }
    }

    @Nested
    inner class UpdateCourse {
        @Test
        fun `updating a course`() {
            val entityCapture = slot<CourseEntity>()
            every { courseRepository.existsByCourseNameIgnoreCase(defaultCourseNameSpanish) } returns false
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity1)
            every { courseRepository.save(capture(entityCapture)) } answers {entityCapture.captured}

            val result = cut.updateCourse(defaultCourseId, defaultCourseNameSpanish)

            val actualEntity = entityCapture.captured
            assertThat(result).isEqualTo(defaultCourseSimpleDTO.copy(courseName = defaultCourseNameSpanish))
            assertThat(actualEntity.courseName).isEqualTo(defaultCourseNameSpanish)
            verify { courseRepository.save(actualEntity) }
        }

        @Test
        fun `throws a CourseAlreadyExistsException when duplicate name exists`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity2)
            every { courseRepository.existsByCourseNameIgnoreCase(defaultCourseNameEnglish) } returns true

            assertThrows<CourseAlreadyExistsException> {
                cut.updateCourse(defaultCourseId, defaultCourseNameEnglish)
            }

            verify(exactly = 0) { courseRepository.save(any()) }
        }

        @Test
        fun `throws a CourseNotFoundException when course does not exist`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.empty()

            assertThrows<CourseNotFoundException> {
                cut.updateCourse(defaultCourseId, defaultCourseNameEnglish)
            }

            verify(exactly = 0) { courseRepository.save(any()) }
        }
    }

    @Nested
    inner class DeleteCourse {
        @Test
        fun `deleting a course`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity1)
            every { courseRepository.deleteById(defaultCourseId) } just Runs

            cut.deleteCourse(defaultCourseId)

            verify { courseRepository.deleteById(defaultCourseId) }
        }

        @Test
        fun `throws a StudentEnrolledInCourseException when students are enrolled in course`() {
            every { courseRepository.findById(courseEntity2.id) } returns Optional.of(courseEntity2)
            every { courseRepository.deleteById(courseEntity2.id) } just Runs

            assertThrows<StudentEnrolledInCourseException> {
                cut.deleteCourse(courseEntity2.id)
            }

            verify(exactly = 0) { courseRepository.deleteById(any()) }
        }

        @Test
        fun `throws CourseNotFoundException when id does not exist`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.empty()

            assertThrows<CourseNotFoundException> {
                cut.deleteCourse(defaultCourseId)
            }
            verify(exactly = 0) { courseRepository.deleteById(any()) }
        }
    }

    @Nested
    inner class AddStudentToCourse {
        @Test
        fun `adding student to course`() {
            val entityCapture = slot<CourseEntity>()
            every { courseRepository.findById(courseEntity1.id) } returns Optional.of(courseEntity1)
            every { studentRepository.findById(studentEntity.id) } returns Optional.of(studentEntity)
            every { courseRepository.save(capture(entityCapture)) } answers { entityCapture.captured }

            cut.addStudentToCourse(courseEntity1.id, studentEntity.id)

            val actual = entityCapture.captured
            assertThat(actual.students).isEqualTo(courseEntity1.students)
            verify { courseRepository.save(actual) }
        }

        @Test
        fun `throws a CourseNotFoundException when Course does not exist`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.empty()

            assertThrows<CourseNotFoundException> {
                cut.addStudentToCourse(
                    defaultCourseId, defaultStudentId
                )
            }

            verify(exactly = 0) { courseRepository.save(any()) }
        }

        @Test
        fun `throws a StudentNotFoundException when Student does not exist`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity1)
            every { studentRepository.findById(defaultStudentId) } returns Optional.empty()

            assertThrows<StudentNotFoundException> {
                cut.addStudentToCourse(defaultCourseId, defaultStudentId)
            }

            verify(exactly = 0) { studentRepository.save(any()) }
        }

        @Test
        fun `throws StudentAlreadyExistsException when student is already enrolled to course`() {
            every { courseRepository.findById(courseEntity2.id) } returns Optional.of(courseEntity2)
            every { studentRepository.findById(studentEntity.id) } returns Optional.of(studentEntity)

            assertThrows<StudentAlreadyExistsException> {
                cut.addStudentToCourse(courseEntity2.id, studentEntity.id)
            }
            verify(exactly = 0) { courseRepository.save(any()) }
        }
    }

    @Nested
    inner class RemoveStudentFromCourse {
        @Test
        fun `removing student from course`() {
            val entityCapture = slot<CourseEntity>()
            every { courseRepository.findById(courseEntity2.id) } returns Optional.of(courseEntity2)
            every { studentRepository.findById(studentEntity.id) } returns Optional.of(studentEntity)
            every { courseRepository.save(capture(entityCapture)) } answers { entityCapture.captured }

            cut.removeStudentFromCourse(courseEntity2.id, studentEntity.id)

            val actual = entityCapture.captured
            assertEquals(courseEntity2.students, actual.students)
            verify { courseRepository.save(actual) }
        }

        @Test
        fun `throws a CourseNotFoundException when Course does not exist`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.empty()

            assertThrows<CourseNotFoundException> {
                cut.removeStudentFromCourse(
                    defaultCourseId, defaultStudentId
                )
            }

            verify(exactly = 0) { courseRepository.save(any()) }
        }

        @Test
        fun `throws a StudentNotFoundException when Student does not exist`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity1)
            every { studentRepository.findById(defaultStudentId) } returns Optional.empty()

            assertThrows<StudentNotFoundException> {
                cut.removeStudentFromCourse(defaultCourseId, defaultStudentId)
            }

            verify(exactly = 0) { studentRepository.save(any()) }
        }

        @Test
        fun `throws StudentNotEnrolledInCourseException when student is not enrolled in the course`() {
            every { courseRepository.findById(courseEntity1.id) } returns Optional.of(courseEntity1)
            every { studentRepository.findById(studentEntity.id) } returns Optional.of(studentEntity)

            assertThrows<StudentNotEnrolledInCourseException> {
                cut.removeStudentFromCourse(courseEntity1.id, studentEntity.id)
            }

            verify(exactly = 0) { courseRepository.save(any()) }
        }
    }
}
