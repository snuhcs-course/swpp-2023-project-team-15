package com.swpp2023.eatandtell.dto

data class EditProfileRequest(
    val description: String? = null,
    val avatar_url: String? = null,
)