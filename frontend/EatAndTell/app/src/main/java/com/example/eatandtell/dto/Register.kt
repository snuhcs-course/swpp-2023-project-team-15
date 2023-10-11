package com.example.eatandtell.dto

import retrofit2.Response

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
)

data class RegisterResponse(val token: String)

data class RegisterResult(
    val token: String,
)