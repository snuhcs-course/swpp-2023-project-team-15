package com.example.eatandtell.dto

data class UploadPostRequest(
    val restaurant : RestReqDTO,
    val photos: List<PhotoReqDTO>? = null,
    val rating: String,
    val description: String,
)

data class GetAllPostsResponse(
    var data : List<PostDTO>
)

data class GetFeedResponse(
    var id : Int,
    val username : String,
    val description: String,
    val avatar_url: String,
    val tags: List<String>,
    val is_followed: Boolean,
    val follower_count: Int,
    val following_count: Int,
    val posts: List<PostDTO>
)

data class toggleLikeResponse(
    val message : String,
)