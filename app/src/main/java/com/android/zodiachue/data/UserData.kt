package com.android.zodiachue.data

data class UserData(
    var firstName: String,
    var lastName: String,
    val birthDate: String,
    val zodiacSign: String,
    val username: String,
    val password: String
)
