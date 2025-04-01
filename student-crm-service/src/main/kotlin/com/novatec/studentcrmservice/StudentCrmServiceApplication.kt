package com.novatec.studentcrmservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StudentCrmServiceApplication

fun main(args: Array<String>) {
	runApplication<StudentCrmServiceApplication>(*args)
}
