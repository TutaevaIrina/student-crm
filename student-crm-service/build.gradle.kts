import kotlinx.kover.api.DefaultIntellijEngine
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.23"

    id("org.asciidoctor.jvm.convert") version "3.3.2"

    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.8.21"
}

group = "de.novatec.je"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

kover {
    engine.set(DefaultIntellijEngine)
    htmlReport {
        onCheck.set(true)
    }
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("ch.qos.logback:logback-core:1.5.6")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.8.1")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.rest-assured:kotlin-extensions")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
}

tasks {
    asciidoctor {
        dependsOn(test)
        baseDirFollowsSourceDir()
        options(
            mapOf(
                "doctype" to "book",
                "backend" to "html5"
            )
        )

        attributes(
            mapOf(
                "snippets" to "$buildFile/generated-snippets",
                "source-highlighter" to "coderay",
                "toclevels" to "3",
                "toc" to "left",
                "sectlinks" to "true",
                "data-uri" to "true",
                "nofooter" to "true"
            )
        )
    }
    asciidoctorj {
        fatalWarnings("include file not found")
        modules { diagram.use() }
    }
    bootJar {
        dependsOn(asciidoctor)
        from(asciidoctor) {
            into("BOOT-INF/classes/static/docs")
        }
        manifest {
            attributes["Implementation-Title"] = "student-crm-service"
            attributes["Implementation-Version"] = archiveVersion
        }
        archiveBaseName.set("student-crm-service")
        archiveVersion.set("") // omit the version from the jar file name
    }
}

configurations.all {
    exclude("org.mockito:mockito-core")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
