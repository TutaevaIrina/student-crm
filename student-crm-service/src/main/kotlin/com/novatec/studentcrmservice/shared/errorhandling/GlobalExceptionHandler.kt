package com.novatec.studentcrmservice.shared.errorhandling

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException) =
        when(ex.cause) {
            is ValueInstantiationException -> respondWith(
                BAD_REQUEST,
                ex.cause?.cause?.message
            ).also { logger.error(ex.message) }
            is MismatchedInputException -> respondWith(
                BAD_REQUEST,
                ex.message?.
                replace("JSON parse error: Instantiation of [simple type, class com.novatec.studentcrmservice.course.api.CourseRequestBodyDTO] ", "")
            ).also { logger.error(ex.message) }
            else -> { null }
        }

    @ExceptionHandler
    fun handleHttpRequestMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException) =
        respondWith(
            BAD_REQUEST,
            ex.message
        ).also { logger.error(ex.message) }

    @ExceptionHandler(StudentNotFoundException::class, CourseNotFoundException::class)
    fun handleNotFoundExceptions(ex: BusinessException) =
        respondWith(
            NOT_FOUND,
            ex.message
        ).also { logger.error(ex.message) }


    @ExceptionHandler(
        CoursenameIsAlreadyExistException::class,
        EmailIsAlreadyExistException::class,
        StudentAlreadyExistsException::class,
        CourseAlreadyExistsException::class,
        StudentDataInUseException::class
    )
    fun handleConflictExceptions(ex: BusinessException) =
        respondWith(CONFLICT, ex.message).also { logger.error("TrackingId: {} message: {}", MDC.get("trackingId"), ex.message) }


    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception) =
        respondWith(INTERNAL_SERVER_ERROR, "An internal error occurred.")
            .also { logger.error(ex.message, ex.javaClass.simpleName) }

    private fun respondWith(responseStatus: HttpStatus, message: String?) =
        ResponseEntity.status(responseStatus).body(message)
}
