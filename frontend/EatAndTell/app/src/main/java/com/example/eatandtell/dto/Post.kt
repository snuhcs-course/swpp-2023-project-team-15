package com.example.eatandtell.dto

data class UploadPostRequest(
    val restaurant : RestReqDTO,
    val photos: List<PhotoReqDTO>,
    val rating: String,
    val description: String,
)

data class GetAllPostsResponse(
    var results : List<PostDTO> //TODO
)