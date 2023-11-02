package com.example.eatandtell.dto

import retrofit2.Response

data class EditProfileRequest(
    val description: String? = null,
    val avatar_url: String? = null,
)