package com.example.eatandtell.dto

data class CreatePostRequest(
    val restaurant : RestReqDTO,
    val photos: List<PhotoReqDTO>,
    val title: String,
    val menu: String,
    val rating: String,
    val description: String,
)

data class GetAllPostsResponse(
    var results : List<PostDTO> //TODO
)