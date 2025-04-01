package de.novatec.itu.studentcrmservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
class StudentCrmServiceApplication

fun main(args: Array<String>) {
	runApplication<StudentCrmServiceApplication>(*args)
}
