package com.example.eatandtell.dto

data class CreatePostRequest(
    val user: Int,
    val restaurant : RestuarantDTO,
    val title: String,
    val menu: String,
    val rating: Float,
    val description: String,
    val photos: List<PhotoDTO>
)

data class GetAllPostsResponse(
    val results: List<PostDTO> //TODO
)

