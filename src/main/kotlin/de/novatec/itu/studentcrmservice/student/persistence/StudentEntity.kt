package de.novatec.itu.studentcrmservice.student.persistence

import de.novatec.itu.studentcrmservice.course.persistence.CourseEntity
import de.novatec.itu.studentcrmservice.student.business.model.StudentDTO
import de.novatec.itu.studentcrmservice.student.business.model.StudentSimpleDTO
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

@Entity
@Table(name = "students")
class StudentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_seq_gen")
    @SequenceGenerator(name = "student_seq_gen", sequenceName = "seq_students_id", allocationSize = 1)
    @Column(name = "id")
    val id: Long,

    @Column(name = "first_name")
    @Size(max = 20)
    var firstName: String,

    @Column(name = "last_name")
    @Size(max = 20)
    var lastName: String,

    @Column(name = "email")
    @Email
    var email: String,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
    name = "students_courses",
    joinColumns = [JoinColumn(name = "students_id", referencedColumnName = "id")],
    inverseJoinColumns = [JoinColumn(name = "courses_id", referencedColumnName = "id")])
    val courses: MutableSet<CourseEntity> = mutableSetOf()
    ) {
    fun toDto(): StudentDTO {
        return StudentDTO(
            id = this.id,
            firstName = this.firstName,
            lastName = this.lastName,
            email = this.email,
            courses = this.courses.map { it.id }.toMutableSet()
        )
    }

    fun toSimpleDto(): StudentSimpleDTO {
        return StudentSimpleDTO(
            id = this.id,
            firstName = this.firstName,
            lastName = this.lastName,
            email = this.email
        )
    }
}
