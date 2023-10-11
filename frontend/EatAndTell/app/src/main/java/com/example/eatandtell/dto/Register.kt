package com.example.eatandtell.dto

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
)

data class RegisterResponse(
    val token: String
)

sealed class RegistrationResult {
    data class Success(val token: String?) : RegistrationResult()
    data class Error(val errorMessage: String) : RegistrationResult()
}