package com.example.eatandtell.dto

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
)
