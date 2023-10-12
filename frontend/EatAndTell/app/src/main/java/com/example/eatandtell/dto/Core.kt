package com.example.eatandtell.dto

import android.media.Image
import retrofit2.http.Url
import java.net.URL

data class UserDTO(
    // val id: Int,
    val username: String,
    val email: String,
    // val profile_img : String,
    // val greeting : String,
)

data class RestaurantDTO(
    val id: Int?,
    val name: String,
)

data class PhotoDTO(
    val id : Int?,
    val photoUrl : String,
    val post : Int?
)

data class RestReqDTO(
    val name: String,
)

data class PhotoReqDTO(
    val photoUrl : String,
)

// upload post시 response이자, get posts시 response list의 요소
data class PostDTO(
    val id: Int,
    val user: Int,
    val restaurant : RestaurantDTO,
    val rating: String, //TODO
    val description: String,
    val photos: List<PhotoDTO>,
    val createdAt : String?, //TODO
)
