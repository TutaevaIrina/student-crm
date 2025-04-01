package de.novatec.itu.studentcrmservice.student.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface StudentRepository : JpaRepository<StudentEntity, Long> {
    @Query ("SELECT s FROM StudentEntity s WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    fun findByNameIgnoreCase(name: String): List<StudentEntity>

    fun existsByEmailIgnoreCase(email: String):Boolean
}
