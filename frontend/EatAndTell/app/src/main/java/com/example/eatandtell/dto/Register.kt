package com.example.eatandtell.dto

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
)

data class RegisterResponse(
    val token: String
)
