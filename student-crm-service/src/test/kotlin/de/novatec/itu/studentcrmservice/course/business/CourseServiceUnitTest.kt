package de.novatec.itu.studentcrmservice.course.business

import de.novatec.itu.studentcrmservice.course.TestDataProvider.courseNameMathe
import de.novatec.itu.studentcrmservice.course.TestDataProvider.courseNameIT
import de.novatec.itu.studentcrmservice.course.TestDataProvider.courseWithStudentIdsDTO1
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseEntity
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseEntityWithStudents
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultCourseId
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultSimpleCourseDTO
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultStudentEntity
import de.novatec.itu.studentcrmservice.course.persistence.CourseEntity
import de.novatec.itu.studentcrmservice.course.persistence.CourseRepository
import de.novatec.itu.studentcrmservice.exceptions.*
import de.novatec.itu.studentcrmservice.student.persistence.StudentRepository
import de.novatec.itu.studentcrmservice.course.TestDataProvider.defaultStudentId
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class CourseServiceUnitTest {
    private val courseRepository = mockk<CourseRepository>(relaxed = true)
    private val studentRepository = mockk<StudentRepository>(relaxed = true)
    private val cut = CourseService(courseRepository, studentRepository)

    @Nested
    inner class FindAllCourses {

        @Test
        fun `getting courses`() {
            every { courseRepository.findAll() } returns listOf(defaultCourseEntity)

            val result = cut.findAllCourses()

            result shouldBe listOf(courseWithStudentIdsDTO1)
            verify { courseRepository.findAll() }
        }

        @Test
        fun `getting empty list of course`() {
            every { courseRepository.findAll() } returns emptyList()

            val result = cut.findAllCourses()

            result shouldBe emptyList()
            verify { courseRepository.findAll() }
        }
    }

    @Nested
    inner class FindCourseById {

        @Test
        fun `getting course by id`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(defaultCourseEntity)

            val result = cut.getCourseById(defaultCourseId)

            result shouldBe defaultCourseEntity.toDto()
            verify { courseRepository.findById(defaultCourseId) }
        }

        @Test
        fun `throws CourseNotFoundException when course does not exist`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.empty()

            val result = assertThrows<CourseNotFoundException> {
                cut.getCourseById(defaultCourseId)
            }

            result.message shouldBe "Course with id $defaultCourseId not found"
            verify { courseRepository.findById(defaultCourseId) }
        }
    }

    @Nested
    inner class CreatingCourse {
        @Test
        fun `creating a course`() {
            val courseCaptor = slot<CourseEntity>()
            every { courseRepository.existsByCourseNameIgnoreCase(courseNameMathe) } returns false
            every { courseRepository.save(capture(courseCaptor)) } answers { courseCaptor.captured }

            val result = cut.createCourse(courseNameMathe)
            val persistedEntity = courseCaptor.captured

            result shouldBe defaultSimpleCourseDTO
            persistedEntity.courseName shouldBe defaultSimpleCourseDTO.courseName
            verify { courseRepository.save(persistedEntity) }
        }

        @Test
        fun `throws a CourseAlreadyExistsException when a course name is already taken`() {
            every { courseRepository.existsByCourseNameIgnoreCase(courseNameMathe) } returns true

            assertThrows<CourseAlreadyExistsException> {
                cut.createCourse(courseNameMathe)
            }

            verify(exactly = 0)  { courseRepository.save(any()) }
        }
    }

    @Nested
    inner class UpdateCourse {

        @Test
        fun `updating a course`() {
            val entityCourseCapture = slot<CourseEntity>()
            val courseEntity = CourseEntity(courseName = courseNameMathe)
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity)
            every { courseRepository.existsByCourseNameIgnoreCase(courseNameMathe) } returns false
            every { courseRepository.save(capture(entityCourseCapture)) } answers { entityCourseCapture.captured }

            val result = cut.updateCourse(defaultCourseId, courseNameIT)

            val persistedEntity = entityCourseCapture.captured
            result shouldBe defaultSimpleCourseDTO.copy(courseName = courseNameIT)
            persistedEntity.toSimpleDTO() shouldBe defaultSimpleCourseDTO.copy(courseName = courseNameIT)
            verify { courseRepository.save(persistedEntity) }
        }

        @Test
        fun `throws a CourseNotFoundException when course does not exist`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.empty()

            assertThrows<CourseNotFoundException> {
                cut.updateCourse(defaultCourseId, courseNameIT)
            }

            verify(exactly = 0) { courseRepository.save(any()) }
        }

        @Test
        fun `throws a CourseAlreadyExistsException when Coursename is already taken`() {
            val courseEntity = CourseEntity(courseName = "Physik")
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity)
            every { courseRepository.existsByCourseNameIgnoreCase(courseNameMathe) } returns true

            assertThrows<CourseAlreadyExistsException> {
                cut.updateCourse(defaultCourseId, courseNameMathe)
            }

            verify(exactly = 0) { courseRepository.save(any()) }
        }
    }

    @Nested
    inner class DeleteCourse {

        @Test
        fun `deleting a course`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(defaultCourseEntity)

            cut.deleteCourse(defaultCourseId)

            verify { courseRepository.findById(defaultCourseId) }
            verify { courseRepository.deleteById(defaultCourseId) }
        }

        @Test
        fun `throws a CourseNotFoundException when a Course is not found`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.empty()
            every { courseRepository.deleteById(any()) } just Runs

            assertThrows<CourseNotFoundException> {
                cut.deleteCourse(defaultCourseId)
            }

            verify { courseRepository.findById(defaultCourseId) }
            verify (exactly = 0) { courseRepository.deleteById(defaultCourseId) }
        }

        @Test
        fun `throws a StudentEnrolledInCourseException when a Course still have Students inside`() {
            val courseEntity = CourseEntity(courseName = courseNameIT, students = mutableListOf(defaultStudentEntity))
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity)

            assertThrows<StudentEnrolledInCourseException> {
                cut.deleteCourse(defaultCourseId)
            }

            verify { courseRepository.findById(defaultCourseId) }
            verify (exactly = 0) { courseRepository.deleteById(defaultCourseId) }
        }
    }

    @Nested
    inner class AddStudentToCourse {

        @Test
        fun `add a student to course`() {
            val entityCapture = slot<CourseEntity>()
            val courseEntity = CourseEntity(courseName = courseNameMathe)

            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity)
            every { studentRepository.findById(defaultStudentId) } returns Optional.of(defaultStudentEntity)
            every { courseRepository.save(capture(entityCapture)) } answers { entityCapture.captured }

            cut.addStudentToCourse(defaultCourseId, defaultStudentId)

            val persistedEntity = entityCapture.captured
            verify { courseRepository.save(persistedEntity) }
        }

        @Test
        fun `throws a CourseNotFoundException when Course does not exist`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.empty()

            assertThrows<CourseNotFoundException> {
                cut.addStudentToCourse(
                    defaultCourseId, defaultStudentId
                )
            }

            verify { courseRepository.findById(defaultCourseId) }
            verify(exactly = 0) { studentRepository.findById(defaultStudentId) }
            verify(exactly = 0) { courseRepository.save(any()) }
        }

        @Test
        fun `throws a StudentNotFoundException when Student does not exist`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(defaultCourseEntity)
            every { studentRepository.findById(defaultStudentId) } returns Optional.empty()

            assertThrows<StudentNotFoundException> {
                cut.addStudentToCourse(defaultCourseId, defaultStudentId)
            }

            verify { studentRepository.findById(defaultStudentId) }
            verify(exactly = 0) { studentRepository.save(any()) }
        }

        @Test
        fun `throws a StudentAlreadyExistsException when a Student is already enrolled to course`() {
            val entityCapture = slot<CourseEntity>()
            val courseEntity = CourseEntity(courseName = courseNameIT, students = mutableListOf(defaultStudentEntity))
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity)
            every { studentRepository.findById(defaultStudentId) } returns Optional.of(defaultStudentEntity)
            every { courseRepository.save(capture(entityCapture)) } answers { entityCapture.captured }

            assertThrows<StudentAlreadyExistsException> {
                cut.addStudentToCourse(defaultCourseId, defaultStudentId)
            }

            verify(exactly = 0) { courseRepository.save(courseEntity) }
        }
    }

    @Nested
    inner class RemoveStudentFromCourse {

        @Test
        fun `removes student from course`() {
            val courseEntity = defaultCourseEntity
            val studentEntity = defaultStudentEntity

            courseEntity.students.add(studentEntity)
            studentEntity.courses.add(courseEntity)


            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity)
            every { studentRepository.findById(defaultStudentId) } returns Optional.of(studentEntity)
            every { courseRepository.save(any()) } returns courseEntity

            cut.removeStudentFromCourse(defaultCourseId, defaultStudentId)

            courseEntity.students.contains(studentEntity) shouldBe false
            studentEntity.courses.contains(courseEntity) shouldBe false

            verify { courseRepository.save(courseEntity) }
        }

        @Test
        fun `throws StudentNotFoundException when student does not exist`() {
            val courseEntity = defaultCourseEntityWithStudents

            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity)
            every { studentRepository.findById(defaultStudentId) } returns Optional.empty()

            assertThrows<StudentNotFoundException> {
                cut.removeStudentFromCourse(defaultCourseId, defaultStudentId)
            }

            verify(exactly = 0) { courseRepository.save(any()) }
        }

        @Test
        fun `throws StudentNotEnrolledInCourseException when student is not enrolled in course`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(defaultCourseEntity)
            every { studentRepository.findById(defaultStudentId) } returns Optional.of(defaultStudentEntity)

            assertThrows<StudentNotEnrolledInCourseException> {
                cut.removeStudentFromCourse(defaultCourseId, defaultStudentId)
            }

            verify(exactly = 0) { courseRepository.save(any()) }
        }
    }
}
