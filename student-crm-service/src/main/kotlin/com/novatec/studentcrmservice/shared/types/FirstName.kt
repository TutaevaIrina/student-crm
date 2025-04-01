package com.novatec.studentcrmservice.shared.types

const val FIRST_NAME_IDENTIFIER = "firstName"
val FIRST_NAME_FIELD_REGEX = Regex("""([a-zA-Z]{2,20})([- ][a-zA-Z]{2,20})*""")

class FirstName (
    firstName: String
) : BasicTextField(firstName) {
    init {
        verifyField(FIRST_NAME_IDENTIFIER, FIRST_NAME_FIELD_REGEX)
    }
}

fun String.toFirstName() = FirstName(this)