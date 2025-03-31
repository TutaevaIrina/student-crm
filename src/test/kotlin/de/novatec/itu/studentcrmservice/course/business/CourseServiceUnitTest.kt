package de.novatec.itu.studentcrmservice.course.business

import de.novatec.itu.studentcrmservice.course.api.CourseRequestDTO
import de.novatec.itu.studentcrmservice.course.persistence.CourseEntity
import de.novatec.itu.studentcrmservice.course.persistence.CourseRepository
import de.novatec.itu.studentcrmservice.exceptions.*
import de.novatec.itu.studentcrmservice.student.persistence.StudentEntity
import de.novatec.itu.studentcrmservice.student.persistence.StudentRepository

import io.mockk.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import java.util.*

class CourseServiceUnitTest {
    private  val courseRepository = mockk<CourseRepository>()
    private  val studentRepository = mockk<StudentRepository>()
    private val cut = CourseService(courseRepository, studentRepository)

    private val studentEntity = StudentEntity(id = 1, "Mark", "Miller", "mark@miller", mutableSetOf())
    private val courseRequestDTO1 = CourseRequestDTO(courseName = "English")
    private val courseRequestDTO2 = CourseRequestDTO(courseName = "German")

    private val courseEntity1 = CourseEntity(
        id = 1,
        courseName = "English",
        students = mutableSetOf()
    )

    private val courseEntity2 = CourseEntity(
        id = 2,
        courseName = "Spanish",
        students = mutableSetOf(studentEntity)
    )

    private val courseEntityList = listOf(courseEntity1, courseEntity2)

    @Nested
    inner class FindAllCourses {
        @Test
        fun `should return list of courses`() {
            every { courseRepository.findAll() } returns courseEntityList
            val expectedList = courseEntityList.map { it.toDto() }
            val result = cut.findAllCourses()
            assertEquals(expectedList, result)
        }

        @Test
        fun `should return empty list of courses when no course exists`() {
            every { courseRepository.findAll() } returns emptyList()
            val expectedList = listOf<CourseEntity>()
            val result = cut.findAllCourses()
            assertEquals(expectedList, result)
        }
    }

    @Nested
    inner class CreateCourse {
        @Test
        fun `should save new course when no duplicate course exists`() {
            val entityCapture = slot<CourseEntity>()
            every { courseRepository.existsByCourseName(courseRequestDTO1.courseName) } returns false
            every { courseRepository.save(capture(entityCapture)) } returns courseEntity1

            val createdCourse = cut.createCourse(courseRequestDTO1.courseName)

            val actualEntity = entityCapture.captured
            assertEquals(courseRequestDTO1.courseName, createdCourse.courseName)
            assertEquals(courseRequestDTO1.courseName, actualEntity.courseName)
            verify { courseRepository.save(actualEntity) }
        }

        @Test
        fun `should not save new course when duplicate course exists`() {
            every { courseRepository.existsByCourseName(courseRequestDTO1.courseName) } returns true
            assertThrows<CourseAlreadyExistsException> {
                cut.createCourse(courseRequestDTO1.courseName)
            }
            verify(exactly = 0) { courseRepository.save(any()) }
        }
    }

    @Nested
    inner class FindCourseById {
        @Test
        fun `should find course by id when id exists`() {
            every { courseRepository.findById(1) } returns Optional.of(courseEntity1)
            val expected = courseEntity1.toSimpleDTO()
            val result = cut.findCourseById(1)
            assertEquals(expected.id, result.id)
            assertEquals(expected.courseName, result.courseName)
        }

        @Test
        fun `should throw exception when no course id exists`() {
            every { courseRepository.findById(any()) } returns Optional.empty()
            assertThrows<CourseNotFoundException> { cut.findCourseById(3) }
        }
    }

    @Nested
    inner class FindCourseByName {
        @Test
        fun `should throw exception when no course found by the given name`() {
            every { courseRepository.findByNameIgnoreCase(any()) } returns emptyList()
            assertThrows<CourseNotFoundException> { cut.findCourseByName("kjkjlkj") }
        }

        @Test
        fun `should return all courses with the given name`() {
            every { courseRepository.findByNameIgnoreCase("ish") } returns courseEntityList
            val expectedList = courseEntityList.map { it.toDto() }
            val result = cut.findCourseByName("ish")
            assertEquals(expectedList, result)
        }
    }

    @Nested
    inner class UpdateCourse {
        @Test
        fun `should update course when no duplicate name exists`() {
            val entityCapture = slot<CourseEntity>()
            every { courseRepository.existsByCourseName(any()) } returns false
            every { courseRepository.findById(1) } returns Optional.of(courseEntity1)
            every { courseRepository.save(capture(entityCapture)) } returns courseEntity1

            val updatedCourse = cut.updateCourse(1, courseRequestDTO1.courseName)

            val actualEntity = entityCapture.captured
            assertEquals(courseRequestDTO1.courseName, updatedCourse.courseName)
            assertEquals(courseRequestDTO1.courseName, actualEntity.courseName)
            verify { courseRepository.save(actualEntity) }
        }

        @Test
        fun `should throw exception when duplicate name exists`() {
            every { courseRepository.existsByCourseName(any()) } returns true
            every { courseRepository.findById(1) } returns Optional.of(courseEntity1)
            every { courseRepository.save(any()) } returns courseEntity1

            assertThrows<CourseAlreadyExistsException> {
                cut.updateCourse(1, courseRequestDTO2.courseName)
            }
            verify(exactly = 0) { courseRepository.save(any())}
        }
    }

    @Nested
    inner class DeleteCourse{
        @Test
        fun `should delete course by id when no students are enrolled`() {
            every { courseRepository.findById(1) } returns Optional.of(courseEntity1)
            every { courseRepository.deleteById(1) } just Runs
            cut.deleteCourse(1)
            verify { courseRepository.deleteById(1) }
        }

        @Test
        fun `should throw exception when students are enrolled in course`() {
            every { courseRepository.findById(2) } returns Optional.of(courseEntity2)
            every { courseRepository.deleteById(2) } just Runs

            assertThrows<StudentEnrolledInCourseException> {
                cut.deleteCourse(2)
            }
            verify(exactly = 0) { courseRepository.deleteById(any()) }
        }
        @Test
        fun `should throw exception when no id exists`() {
            every { courseRepository.findById(any()) } returns Optional.empty()

            assertThrows<CourseNotFoundException> {
                cut.deleteCourse(3)
            }
            verify(exactly = 0) { courseRepository.deleteById(any()) }
        }
    }

    @Nested
    inner class AddStudentToCourse{
        @Test
        fun `should add student to course`() {
            val entityCapture = slot<CourseEntity>()
            every { courseRepository.findById(1) } returns Optional.of(courseEntity1)
            every { studentRepository.findById(1) } returns Optional.of(studentEntity)
            every { courseRepository.save(capture(entityCapture))} answers { entityCapture.captured }

            cut.addStudentToCourse(courseEntity1.id, studentEntity.id)

            val actual = entityCapture.captured
            assertTrue(courseEntity1.students.contains(studentEntity))
            assertTrue(studentEntity.courses.contains(courseEntity1))
            assertEquals(courseEntity1.students, actual.students)
            verify { courseRepository.save(actual) }

        }
        @Test
        fun `should throw exception if student is already enrolled to course`() {
            every { courseRepository.findById(2) } returns Optional.of(courseEntity2)
            every { studentRepository.findById(1) } returns Optional.of(studentEntity)

            assertThrows<StudentAlreadyExistsException> {
                cut.addStudentToCourse(courseEntity2.id, studentEntity.id)
            }
            verify(exactly = 0) { courseRepository.save(any()) }
        }
    }
    @Nested
    inner class RemoveStudentFromCourse{
        @Test
        fun `should remove student from the course`() {
            val entityCapture = slot<CourseEntity>()
            every { courseRepository.findById(2) } returns Optional.of(courseEntity2)
            every { studentRepository.findById(1) } returns Optional.of(studentEntity)
            every { courseRepository.save(capture(entityCapture))} answers { entityCapture.captured }

            cut.removeStudentFromCourse(courseEntity2.id, studentEntity.id)

            val actual = entityCapture.captured
            assertTrue(courseEntity1.students.isEmpty())
            assertTrue(studentEntity.courses.isEmpty())
            assertEquals(courseEntity1.students, actual.students)
            verify { courseRepository.save(actual) }
        }

        @Test
        fun `should throw exception when student is not enrolled in the course`() {
            every { courseRepository.findById(1) } returns Optional.of(courseEntity1)
            every { studentRepository.findById(1) } returns Optional.of(studentEntity)

            assertThrows<StudentNotFoundException> {
                cut.removeStudentFromCourse(courseEntity1.id, studentEntity.id)
            }
            verify(exactly = 0) { courseRepository.save(any()) }
        }
    }
}
