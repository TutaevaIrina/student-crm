package com.novatec.studentcrmservice.shared.types

const val LAST_NAME_IDENTIFIER = "lastName"
val LAST_NAME_FIELD_REGEX = Regex("""([a-zA-Z]{2,20})([- ][a-zA-Z]{2,20})*""")
class LastName (
    lastName: String
) : BasicTextField(lastName) {
    init {
        verifyField(LAST_NAME_IDENTIFIER, LAST_NAME_FIELD_REGEX)
    }
}

fun String.toLastName() = LastName(this)