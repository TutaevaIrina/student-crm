package com.novatec.studentcrmservice.course.persistence

import com.novatec.studentcrmservice.shared.types.CourseName
import com.novatec.studentcrmservice.student.persistence.StudentEntity
import jakarta.persistence.*

@Entity
@Table(name = "courses")
class CourseEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long = 0L,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "courseName", nullable = false, unique = true))
    var courseName: CourseName,

    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val students: MutableList<StudentEntity> = mutableListOf()
)
