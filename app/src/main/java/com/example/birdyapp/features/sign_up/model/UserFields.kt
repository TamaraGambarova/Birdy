package com.example.birdyapp.features.sign_up.model

import androidx.lifecycle.MutableLiveData
import java.util.*

class UserFields {
    val firstName = MutableLiveData<String>()
    val middleName = MutableLiveData<String>()
    val lastName = MutableLiveData<String>()
    val birthdayDate = MutableLiveData<Date>()
    val city = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val passwordRepeated = MutableLiveData<String>()

    fun validate(): Boolean {
        return !(firstName.value.isNullOrBlank()
                || lastName.value.isNullOrBlank()
                || birthdayDate.value == null
                || city.value.isNullOrBlank()
                || email.value.isNullOrBlank()
                || password.value.isNullOrBlank()
                || passwordRepeated.value.isNullOrBlank())
    }
}