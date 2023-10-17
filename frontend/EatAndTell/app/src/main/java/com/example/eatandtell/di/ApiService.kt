package com.example.eatandtell.di

import com.example.eatandtell.dto.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

interface ApiService {
    @POST("users/login/") // The login endpoint (hypothetical)
    fun loginUser(@Body loginData: LoginRequest): Call<LoginResponse>

    @POST("users/register/") // The registration endpoint
    fun registerUser(@Body registrationData: RegisterRequest): Call<RegisterResponse>

    @POST("posts/") // The registration endpoint
    fun uploadPost(@Body postData: UploadPostRequest): Call<PostDTO>

}