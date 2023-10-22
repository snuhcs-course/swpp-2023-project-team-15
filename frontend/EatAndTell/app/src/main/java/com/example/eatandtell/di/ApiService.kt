package com.example.eatandtell.di

import com.example.eatandtell.dto.*
import okhttp3.MultipartBody

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("users/login/") // The login endpoint (hypothetical)
    suspend fun loginUser(@Body loginData: LoginRequest): LoginResponse

    @POST("users/register/") // The registration endpoint
    suspend fun registerUser(@Body registrationData: RegisterRequest): RegisterResponse

    @POST("posts/") // The posts endpoint
    suspend fun uploadPost(@Header("Authorization") authorization: String,
                           @Body postData: UploadPostRequest): PostDTO
    @Multipart
    @POST("images/upload/") // The images endpoint
    suspend fun getImageURL(@Header("Authorization") authorization: String,
                            @Part images: MultipartBody.Part?): ImageURLResponse

    @GET("posts/") // The posts endpoint
    suspend fun getAllPosts(@Header("Authorization") authorization: String,
                           ): GetAllPostsResponse

    @GET("users/me/") // The posts endpoint //TODO: change to getMyPosts
    suspend fun getMyFeed(@Header("Authorization") authorization: String,
    ): GetFeedResponse

    @GET("users/{id}/") // The users endpoint
    suspend fun getUserProfile(@Header("Authorization") authorization: String,
                            @Path("id") id: Int): GetFeedResponse

    @GET("users/filter")
    suspend fun getFilteredUsers(@Header("Authorization") authorization: String,@Query("username") username: String): List<UserDTO>

    @GET("posts/")
    suspend fun getFilteredByRestaurants(@Header("Authorization") authorization: String,@Query("restaurant_name") restaurantName: String): GetAllPostsResponse

    @PUT("posts/{post_id}/likes/") // The posts endpoint
    suspend fun toggleLike(@Header("Authorization") authorization: String,
                           @Path("post_id") post_id: Int): toggleLikeResponse

}