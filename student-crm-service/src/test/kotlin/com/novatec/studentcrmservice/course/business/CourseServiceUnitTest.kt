package com.novatec.studentcrmservice.course.business

import com.novatec.studentcrmservice.course.TestDataProvider.courseNameMathe
import com.novatec.studentcrmservice.course.TestDataProvider.courseNameIT
import com.novatec.studentcrmservice.course.TestDataProvider.defaultCourseDTO
import com.novatec.studentcrmservice.course.TestDataProvider.defaultCourseEntity
import com.novatec.studentcrmservice.course.TestDataProvider.defaultCourseId
import com.novatec.studentcrmservice.course.TestDataProvider.defaultSimpleCourseDTO
import com.novatec.studentcrmservice.course.TestDataProvider.firstName
import com.novatec.studentcrmservice.course.TestDataProvider.lastName
import com.novatec.studentcrmservice.course.TestDataProvider.defaultStudentEntity
import com.novatec.studentcrmservice.course.business.model.CourseDTO
import com.novatec.studentcrmservice.course.business.model.toCourseDto
import com.novatec.studentcrmservice.course.business.model.toSimpleCourseDto
import com.novatec.studentcrmservice.course.persistence.CourseEntity
import com.novatec.studentcrmservice.course.persistence.CourseRepository
import com.novatec.studentcrmservice.shared.errorhandling.*
import com.novatec.studentcrmservice.shared.types.CourseName
import com.novatec.studentcrmservice.shared.types.Email
import com.novatec.studentcrmservice.shared.types.FirstName
import com.novatec.studentcrmservice.shared.types.LastName
import com.novatec.studentcrmservice.student.business.model.SimpleStudentDTO
import com.novatec.studentcrmservice.student.persistence.StudentEntity
import com.novatec.studentcrmservice.student.persistence.StudentRepository
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.Optional

class CourseServiceUnitTest {
    private val courseRepository = mockk<CourseRepository>(relaxed = true)
    private val studentRepository = mockk<StudentRepository>(relaxed = true)
    private val cut = CourseService(courseRepository, studentRepository)

    @Nested
    inner class GetAllCourses {

        @Test
        fun `getting courses`() {
            every { courseRepository.findAll() } returns listOf(defaultCourseEntity)

            val result = cut.getAllCourses()

            result shouldBe listOf(defaultCourseDTO)
            verify { courseRepository.findAll() }
        }

        @Test
        fun `getting empty list of course`() {
            every { courseRepository.findAll() } returns emptyList()

            val result = cut.getAllCourses()

            result shouldBe emptyList()
            verify { courseRepository.findAll() }
        }
    }

    @Nested
    inner class GetCourseById {

        @Test
        fun `getting course by id`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(defaultCourseEntity)

            val result = cut.getCourseById(defaultCourseId)

            result shouldBe defaultCourseEntity.toCourseDto()
            verify { courseRepository.findById(defaultCourseId) }
        }

        @Test
        fun `throws CourseNotFoundException when course does not exist`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.empty()

            assertThrows<CourseNotFoundException> {
                cut.getCourseById(defaultCourseId)
            }

            verify { courseRepository.findById(defaultCourseId) }
        }
    }

    @Nested
    inner class CreatingCourse {

        val courseCaptor = slot<CourseEntity>()

        @BeforeEach
        fun setUp() {
            every { courseRepository.save(capture(courseCaptor)) } answers { courseCaptor.captured }
        }

        @Test
        fun `creating a course`() {
            every { courseRepository.existsByCourseName(courseNameMathe) } returns false

            val result = cut.createNewCourse(courseNameMathe)

            val persistedEntity = courseCaptor.captured
            result shouldBe defaultSimpleCourseDTO
            persistedEntity.courseName shouldBe defaultSimpleCourseDTO.courseName
            verify { courseRepository.save(persistedEntity) }
        }

        @Test
        fun `throws a CourseNameIsAlreadyExistException when a Coursename is already taken`() {
            every { courseRepository.existsByCourseName(courseNameMathe) } returns true

            assertThrows<CoursenameIsAlreadyExistException> {
                cut.createNewCourse(courseNameMathe)
            }

            verify { courseRepository.save(any()) wasNot called }
        }

        @ParameterizedTest
        @ValueSource(strings = ["", "M", "Matheuhuhuhuhuhuhuhuhuhuhzzzzzz", "§"])
        fun `throws an InvalidInputException when a Coursname has invalid input`(example: String) {

            assertThrows<InvalidInputException> {
                cut.createNewCourse(
                    CourseName(courseName = example)
                )
            }

            verify { courseRepository.save(any()) wasNot called }
        }

        @ParameterizedTest
        @ValueSource(strings = ["Mathe", "Mathe2", "Programming for Beginners", "Hoch-Mathe2"])
        fun `does not throw an InvalidInputException when a Coursname has valid input`(example: String) {
            every { courseRepository.existsByCourseName(any()) } returns false

            val result = cut.createNewCourse(CourseName(courseName = example))

            val persistedEntity = courseCaptor.captured
            result shouldBe defaultSimpleCourseDTO.copy(courseName = CourseName(example))
            persistedEntity.courseName shouldBe CourseName(courseName = example)
            verify { courseRepository.save(persistedEntity) }
        }
    }

    @Nested
    inner class UpdateCourse {

        @Test
        fun `updating a course`() {
            val entityCourseCapture = slot<CourseEntity>()
            val courseEntity = CourseEntity(courseName = courseNameMathe)
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity)
            every { courseRepository.existsByCourseName(courseNameMathe) } returns false
            every { courseRepository.save(capture(entityCourseCapture)) } answers { entityCourseCapture.captured }

            val result = cut.updateCourse(defaultCourseId, courseNameIT)

            val persistedEntity = entityCourseCapture.captured
            result shouldBe defaultSimpleCourseDTO.copy(courseName = courseNameIT)
            persistedEntity.toSimpleCourseDto() shouldBe defaultSimpleCourseDTO.copy(courseName = courseNameIT)
            verify { courseRepository.save(persistedEntity) }
        }

        @Test
        fun `throws a CourseNotFoundException when course does not exist`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.empty()

            assertThrows<CourseNotFoundException> {
                cut.updateCourse(defaultCourseId, courseNameIT)
            }

            verify { courseRepository.save(any()) wasNot called }
        }

        @Test
        fun `throws a CourseIsAlreadyExistException when Coursename is already taken`() {
            val courseEntity = CourseEntity(courseName = courseNameMathe)
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity)
            every { courseRepository.existsByCourseName(courseNameMathe) } returns true

            assertThrows<CoursenameIsAlreadyExistException> {
                cut.updateCourse(defaultCourseId, courseNameMathe)
            }

            verify { courseRepository.save(any()) wasNot called }
        }
    }

    @Nested
    inner class AddStudentToCourse {

        @Test
        fun `add a Student to course`() {
            val entityCapture = slot<CourseEntity>()
            val courseEntity = CourseEntity(courseName = CourseName("Mathe"))
            val studentEntity = StudentEntity(
                firstName = FirstName("Jorge"),
                lastName = LastName("Esteves"),
                email = Email("j.esteves@web.de")
            )

            val courseDTO = CourseDTO(
                id = 0L,
                courseName = CourseName("Mathe"),
                students = mutableListOf(
                    SimpleStudentDTO(
                        id = 0L,
                        firstName = FirstName("Jorge"),
                        lastName = LastName("Esteves"),
                        email = Email("j.esteves@web.de")
                    )
                )
            )

            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity)
            every { studentRepository.findByFirstNameAndLastName(firstName, lastName) } returns Optional.of(
                studentEntity
            )
            every { courseRepository.save(capture(entityCapture)) } answers { entityCapture.captured }

            val result = cut.addStudentToCourse(defaultCourseId, firstName, lastName)

            val persistedEntity = entityCapture.captured
            result shouldBe courseDTO
            verify { courseRepository.save(persistedEntity) }
        }

        @Test
        fun `throws a CourseNotFoundException when Course does not exist`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.empty()

            assertThrows<CourseNotFoundException> {
                cut.addStudentToCourse(
                    defaultCourseId, firstName, lastName
                )
            }

            verify { courseRepository.findById(defaultCourseId) }
            verify(exactly = 0) { studentRepository.findByFirstNameAndLastName(firstName, lastName) }
            verify(exactly = 0) { courseRepository.save(any()) }
        }

        @Test
        fun `throws a StudentNotFoundException when Student does not exist`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(defaultCourseEntity)
            every { studentRepository.findByFirstNameAndLastName(firstName, lastName) } returns Optional.empty()

            assertThrows<StudentNotFoundException> {
                cut.addStudentToCourse(defaultCourseId, firstName, lastName)
            }

            verify { studentRepository.findByFirstNameAndLastName(firstName, lastName) }
            verify(exactly = 0) { studentRepository.save(any()) }
        }

        @Test
        fun `throws a StudentAlreadyExistsException when a Student is already enrolled to course`() {
            val entityCapture = slot<CourseEntity>()
            val courseEntity = CourseEntity(courseName = courseNameIT, students = mutableListOf(defaultStudentEntity))
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity)
            every { studentRepository.findByFirstNameAndLastName(firstName, lastName) } returns Optional.of(
                defaultStudentEntity
            )
            every { courseRepository.save(capture(entityCapture)) } answers { entityCapture.captured }

            assertThrows<StudentAlreadyExistsException> {
                cut.addStudentToCourse(defaultCourseId, firstName, lastName)
            }

            verify { courseRepository.save(courseEntity) wasNot called }
        }
    }

    @Nested
    inner class DeleteCourse {

        @Test
        fun `deleting a course`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(defaultCourseEntity)

            val result = cut.deleteCourseById(defaultCourseId)

            result shouldBe defaultSimpleCourseDTO
            verify { courseRepository.findById(defaultCourseId) }
            verify { courseRepository.deleteById(defaultCourseId) }
        }

        @Test
        fun `throws a CourseNotFoundException when a Course is not found`() {
            every { courseRepository.findById(defaultCourseId) } returns Optional.empty()

            assertThrows<CourseNotFoundException> {
                cut.deleteCourseById(defaultCourseId)
            }

            verify { courseRepository.findById(defaultCourseId) }
            verify(exactly = 0) {
                courseRepository.deleteById(defaultCourseId)
            }
        }

        @Test
        fun `throws a StudentDataInUseException when a Course still have Students inside`() {
            val courseEntity = CourseEntity(courseName = courseNameIT, students = mutableListOf(defaultStudentEntity))
            every { courseRepository.findById(defaultCourseId) } returns Optional.of(courseEntity)

            assertThrows<StudentDataInUseException> {
                cut.deleteCourseById(defaultCourseId)
            }

            verify { courseRepository.findById(defaultCourseId) }
            verify(exactly = 0) {
                courseRepository.deleteById(defaultCourseId)
            }
        }
    }
}
