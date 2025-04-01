package de.novatec.itu.studentcrmservice.exceptions

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(exception: MethodArgumentNotValidException) =
        respondWith(
            BAD_REQUEST,
            exception.fieldError?.defaultMessage.toString()
        ).also { logger.warn(exception.message) }

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(exception: BusinessException) =
        when (exception) {
            is EmailAlreadyExistsException,
            is CourseAlreadyExistsException,
            is StudentAlreadyExistsException,
            is StudentEnrolledInCourseException,
            is StudentNotEnrolledInCourseException-> respondWith(
                CONFLICT,
                exception.message
            )

            is CourseNotFoundException,
            is StudentNotFoundException -> respondWith(
                NOT_FOUND,
                exception.message
            )
        }.also { logger.warn(exception.message) }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(exception: HttpMessageNotReadableException) =
        respondWith(BAD_REQUEST, "The body contains invalid values")
            .also { logger.warn(exception.message) }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(exception: Exception) =
        respondWith(INTERNAL_SERVER_ERROR, "An internal error occurred")
            .also { logger.error(exception.message, exception.javaClass.simpleName) }

    private fun respondWith(responseStatus: HttpStatus, message: String) =
        ResponseEntity.status(responseStatus).body(ErrorWrapper(message))
}

data class ErrorWrapper(
    val errorMessage: String
)
