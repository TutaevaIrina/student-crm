package com.novatec.studentcrmservice.shared.types

const val EMAIL_NAME_IDENTIFIER = "email"
val EMAIL_NAME_FIELD_REGEX = Regex("^[\\w!#\$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#\$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")
class Email (
    email: String
) : BasicTextField(email) {
    init {
        verifyField(EMAIL_NAME_IDENTIFIER, EMAIL_NAME_FIELD_REGEX)
    }
}

fun String.toEmail() = Email(this)