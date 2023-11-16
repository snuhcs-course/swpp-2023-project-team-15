package com.example.eatandtell.dto

data class UserDTO(
    val id: Int,
    val username: String,
    val description: String,
    val avatar_url: String,
    val tags: List<String>,
)

data class UserInfoDTO(
    val id : Int,
    val username : String,
    val description : String,
    val avatar_url : String,
    val tags : List<String>,
    val is_followed : Boolean,
    val follower_count : Int,
    val following_count : Int,
)

//우리 서버에 저장되는 형태
data class RestaurantDTO(
    val id: Int?,
    val name: String,
)

//restaurant 요청 시, id 모를 때
data class RestReqDTO(
    val name: String,
    val search_id : Int?,
    val category_name : String? = null,
)


data class PhotoDTO(
    val id : Int?,
    val photo_url : String,
    val post : Int?
)

data class PhotoReqDTO(
    val photo_url : String,
)

// upload post시 response이자, get posts시 response list의 요소
data class PostDTO(
    val id: Int,
    val user: UserDTO,
    val restaurant : RestaurantDTO,
    val rating: String,
    val description: String,
    val photos: List<PhotoDTO>?,
    val created_at : String?,
    val is_liked : Boolean,
    val like_count : Int,
    val tags: List<String>, //this tag is for posts, not users!!
)

data class TagsDTO(
    val user_tags : List<String>
)

data class TopTag(
    val ko_label: String,
    val en_label: String,
    val type: String
)