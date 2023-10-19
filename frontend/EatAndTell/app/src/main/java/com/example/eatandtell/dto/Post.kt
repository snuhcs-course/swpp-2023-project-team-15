package com.example.eatandtell.dto

data class UploadPostRequest(
    val restaurant : RestReqDTO,
    val photos: List<PhotoReqDTO>? = null,
    val rating: String,
    val description: String,
)

data class GetAllPostsResponse(
    var data : List<PostDTO> //TODO
)