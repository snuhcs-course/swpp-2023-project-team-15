package com.example.eatandtell.dto

import retrofit2.Response

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
)