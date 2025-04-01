package com.novatec.studentcrmservice.student.persistence

import com.novatec.studentcrmservice.shared.types.Email
import com.novatec.studentcrmservice.shared.types.FirstName
import com.novatec.studentcrmservice.shared.types.LastName
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface StudentRepository : CrudRepository<StudentEntity, Long> {

    fun existsByEmail(email: Email): Boolean
    fun findByFirstNameAndLastName(firstName: FirstName, lastName: LastName): Optional<StudentEntity>
}
