package com.novatec.studentcrmservice.utils

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.containers.PostgreSQLContainer
import java.util.*

@Retention
@Target(AnnotationTarget.CLASS)
@ContextConfiguration(initializers = [PostgreSQLInitializer::class])
annotation class InitializeWithContainerizedPostgreSQL

class PostgreSQLInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object {
        private val container: PostgreSQLContainer<*> by lazy {
            PostgreSQLContainer("postgres:16.2")
                .apply { start() }
        }
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val database = randomDatabaseName()
        val jdbcUrl = "jdbc:postgresql://${container.host}:${container.firstMappedPort}/$database"

        initializeDatabase(database)

        val urlProperty = "spring.datasource.url=$jdbcUrl"
        val usernameProperty = "spring.datasource.username=${container.username}"
        val passwordProperty = "spring.datasource.password=${container.password}"

        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
            applicationContext,
            urlProperty,
            usernameProperty,
            passwordProperty
        )
    }

    private fun initializeDatabase(database: String) {
        container.createConnection("")
            .use { connection ->
                connection.createStatement()
                    .use { statement ->
                        statement.execute("CREATE DATABASE $database")
                    }
            }
    }

    private fun randomDatabaseName() = "test_${UUID.randomUUID()}".replace("-", "")

}
