package com.example.eatandtell.dto

import android.media.Image
import retrofit2.http.Url
import java.net.URL

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

data class RestaurantDTO(
    val id: Int?,
    val name: String,
)

data class PhotoDTO(
    val id : Int?,
    val photo_url : String,
    val post : Int?
)

data class RestReqDTO(
    val name: String,
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