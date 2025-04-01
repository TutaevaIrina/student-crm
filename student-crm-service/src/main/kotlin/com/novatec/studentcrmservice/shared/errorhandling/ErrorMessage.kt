package com.novatec.studentcrmservice.shared.errorhandling

class ErrorMessage {
    companion object {
        val FIELD_CAN_NOT_BE_BLANK_MESSAGE = "The Field [%s] can not be blank."
        val FIELD_REGEX_DOES_NOT_MATCH_MESSAGE = "The value [%s] is not a valid [%s] because pattern [%s] is required."
    }
}