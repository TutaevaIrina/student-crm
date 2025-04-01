package de.novatec.itu.studentcrmservice.course.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CourseRepository : JpaRepository<CourseEntity, Long> {
    @Query("SELECT c FROM CourseEntity c WHERE LOWER(c.courseName) LIKE LOWER(CONCAT('%', :courseName, '%'))")
    fun findByNameIgnoreCase(courseName: String): List<CourseEntity>

    fun existsByCourseNameIgnoreCase(courseName: String): Boolean
}
