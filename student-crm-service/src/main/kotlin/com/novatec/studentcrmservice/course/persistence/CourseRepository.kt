package com.novatec.studentcrmservice.course.persistence

import com.novatec.studentcrmservice.shared.types.CourseName
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CourseRepository : CrudRepository<CourseEntity, Long> {

    fun existsByCourseName(courseName: CourseName): Boolean
    fun findByCourseName(courseName: CourseName): Optional<CourseEntity>
}
