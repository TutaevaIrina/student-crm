package de.novatec.itu.studentcrmservice.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object ObjectMapper {

    fun objectMapper(objectClass: Any): String {
        return jacksonObjectMapper().writeValueAsString(objectClass)
    }
}