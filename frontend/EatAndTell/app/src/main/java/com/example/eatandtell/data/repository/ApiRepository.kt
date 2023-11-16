package com.example.eatandtell.data.repository

import com.example.eatandtell.data.api.ApiService
import com.example.eatandtell.dto.EditProfileRequest
import com.example.eatandtell.dto.GetAllPostsResponse
import com.example.eatandtell.dto.GetFeedResponse
import com.example.eatandtell.dto.GetSearchedRestResponse
import com.example.eatandtell.dto.ImageURLResponse
import com.example.eatandtell.dto.LoginRequest
import com.example.eatandtell.dto.LoginResponse
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.RegisterRequest
import com.example.eatandtell.dto.RegisterResponse
import com.example.eatandtell.dto.TagsDTO
import com.example.eatandtell.dto.TopTag
import com.example.eatandtell.dto.UploadPostRequest
import com.example.eatandtell.dto.UserDTO
import com.example.eatandtell.dto.toggleFollowResponse
import com.example.eatandtell.dto.toggleLikeResponse
import okhttp3.MultipartBody
import javax.inject.Inject

class ApiRepository@Inject constructor(private val apiService: ApiService) {

    suspend fun loginUser(loginData: LoginRequest): Result<LoginResponse> = runCatching {
        apiService.loginUser(loginData)
    }

    suspend fun registerUser(registrationData: RegisterRequest): Result<RegisterResponse> = runCatching {
        apiService.registerUser(registrationData)
    }

    suspend fun editProfile(authorization: String, profileData: EditProfileRequest): Result<UserDTO> = runCatching {
        apiService.editProfile(authorization, profileData)
    }

    suspend fun uploadPost(authorization: String, postData: UploadPostRequest): Result<PostDTO> = runCatching {
        apiService.uploadPost(authorization, postData)
    }
    suspend fun deletePost( authorization: String, post_id: Int): Result<PostDTO> = runCatching{
        apiService.deletePost(authorization,post_id)
    }

    suspend fun getImageURL(authorization: String, images: MultipartBody.Part?): Result<ImageURLResponse> = runCatching {
        apiService.getImageURL(authorization, images)
    }

    suspend fun getAllPosts(authorization: String): Result<GetAllPostsResponse> = runCatching {
        apiService.getAllPosts(authorization)
    }


    suspend fun getLikedFeed(authorization: String,): Result<List<PostDTO>> = runCatching{
        apiService.getLikedFeed(authorization)
    }

    suspend fun getMyFeed(authorization: String): Result<GetFeedResponse> = runCatching {
        apiService.getMyFeed(authorization)
    }

    suspend fun getUserFeed(authorization: String, id: Int): Result<GetFeedResponse> = runCatching {
        apiService.getUserFeed(authorization, id)
    }

    suspend fun getFilteredUsersByName(authorization: String, username: String): Result<List<UserDTO>> = runCatching {
        apiService.getFilteredUsersByName(authorization, username)
    }

    suspend fun getFilteredUsersByTag(authorization: String, tag: String): Result<List<UserDTO>> = runCatching {
        apiService.getFilteredUsersByTag(authorization, tag)
    }

    suspend fun getFilteredByRestaurants(authorization: String, restaurantName: String): Result<GetAllPostsResponse> = runCatching {
        apiService.getFilteredByRestaurants(authorization, restaurantName)
    }

    suspend fun toggleFollow( authorization: String, user_id: Int): Result<toggleFollowResponse> = runCatching{
        apiService.toggleFollow(authorization,user_id)
    }

    suspend fun toggleLike(authorization: String, postId: Int): Result<toggleLikeResponse> = runCatching {
        apiService.toggleLike(authorization, postId)
    }

    suspend fun refreshTags(authorization: String): Result<TagsDTO> = runCatching {
        apiService.refreshTags(authorization)
    }
    suspend fun getSearchedRest( authorization: String,
                                 query: String,
                                 x: String?,
                                 y: String?): Result<GetSearchedRestResponse> = runCatching {
        apiService.getSearchedRest(authorization,query,x,y)
    }

    suspend fun getTopTags( authorization: String,): Result<List<TopTag>> = runCatching{
        apiService.getTopTags(authorization)
    }



}
