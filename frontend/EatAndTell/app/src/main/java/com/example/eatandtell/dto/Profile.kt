package com.example.eatandtell.dto

import retrofit2.Response

data class EditProfileRequest(
    val username: String,
    val description: String,
    val avatar_url: String,
)