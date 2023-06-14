package com.example.idrecognizer.common

enum class MrzInfoType {
    PERSONAL_NO, EXPIRY_DATE, FIRST_NAME, LAST_NAME, BIRTHDATE, GENDER;

    val requiredSampleSize: Int
        get() {
            return when (this) {
                PERSONAL_NO -> 5
                EXPIRY_DATE -> 5
                FIRST_NAME -> 5
                LAST_NAME -> 5
                BIRTHDATE -> 5
                GENDER -> 1
            }
        }
}