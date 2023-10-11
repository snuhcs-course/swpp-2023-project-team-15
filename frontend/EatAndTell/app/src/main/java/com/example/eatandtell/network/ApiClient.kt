package com.example.eatandtell.network
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
object ApiClient {
    private const val BASE_URL= "http://ec2-13-125-91-166.ap-northeast-2.compute.amazonaws.com/"
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // actual backend URL
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
}