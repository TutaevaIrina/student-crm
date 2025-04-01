package com.novatec.studentcrmservice.shared.types

import com.fasterxml.jackson.annotation.JsonValue
import com.novatec.studentcrmservice.shared.errorhandling.ErrorMessage.Companion.FIELD_CAN_NOT_BE_BLANK_MESSAGE
import com.novatec.studentcrmservice.shared.errorhandling.ErrorMessage.Companion.FIELD_REGEX_DOES_NOT_MATCH_MESSAGE
import com.novatec.studentcrmservice.shared.errorhandling.InvalidInputException
import jakarta.persistence.Embeddable
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
@Embeddable
abstract class BasicTextField(
    @get:JsonValue
    var value: String
) {
    protected fun verifyField(identifierName: String, identifierRegex: Regex) {
        if (value.isBlank()) {
            throw InvalidInputException(FIELD_CAN_NOT_BE_BLANK_MESSAGE.format(identifierName))
        }

        if (!value.matches(identifierRegex)) {
            throw InvalidInputException(
                FIELD_REGEX_DOES_NOT_MATCH_MESSAGE.format(
                    value,
                    identifierName,
                    identifierRegex.pattern
                )
            )
        }
    }

    override fun toString() = value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BasicTextField

        return value == other.value
    }

    override fun hashCode() = value.hashCode()
}
