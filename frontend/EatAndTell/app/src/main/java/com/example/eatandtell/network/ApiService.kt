package com.example.eatandtell.network

import com.example.eatandtell.ui.login.LoginRequest
import com.example.eatandtell.ui.login.LoginResponse
import com.example.eatandtell.ui.signup.RegisterRequest
import com.example.eatandtell.ui.signup.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("users/login/") // The login endpoint
    fun loginUser(@Body loginData: LoginRequest): Call<LoginResponse>

    @POST("users/register/") // The registration endpoint
    fun registerUser(@Body registrationData: RegisterRequest): Call<RegisterResponse>
}