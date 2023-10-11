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

data class RestuarantDTO(
    // val id: Int,
    val name: String,
)

data class PhotoDTO(
    val photo_url : String,
)

data class PostDTO(
    val user: Int,
    val restaurant : RestuarantDTO,
    val title: String,
    val menu: String,
    val rating: Float,
    val description: String,
    val photos: List<PhotoDTO>
)
