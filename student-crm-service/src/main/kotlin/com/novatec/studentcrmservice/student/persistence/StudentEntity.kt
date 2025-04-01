package com.novatec.studentcrmservice.student.persistence

import com.novatec.studentcrmservice.course.persistence.CourseEntity
import com.novatec.studentcrmservice.shared.types.Email
import com.novatec.studentcrmservice.shared.types.FirstName
import com.novatec.studentcrmservice.shared.types.LastName
import jakarta.persistence.*

@Entity
@Table(name = "students")
class StudentEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long = 0L,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "firstName", nullable = false))
    var firstName: FirstName,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "lastName", nullable = false))
    var lastName: LastName,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "email", nullable = false, unique = true))
    var email: Email,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "student_courses",
        joinColumns = [JoinColumn(name = "student_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "course_id", referencedColumnName = "id")]
    )
    val courses: MutableList<CourseEntity> = mutableListOf()
)
