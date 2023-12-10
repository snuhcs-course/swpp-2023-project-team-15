package com.swpp2023.eatandtell.data.repository

import com.swpp2023.eatandtell.data.api.ApiService
import com.swpp2023.eatandtell.dto.EditProfileRequest
import com.swpp2023.eatandtell.dto.GetAllPostsResponse
import com.swpp2023.eatandtell.dto.GetFeedResponse
import com.swpp2023.eatandtell.dto.GetSearchedRestResponse
import com.swpp2023.eatandtell.dto.ImageURLResponse
import com.swpp2023.eatandtell.dto.LoginRequest
import com.swpp2023.eatandtell.dto.LoginResponse
import com.swpp2023.eatandtell.dto.PostDTO
import com.swpp2023.eatandtell.dto.RegisterRequest
import com.swpp2023.eatandtell.dto.RegisterResponse
import com.swpp2023.eatandtell.dto.TagsDTO
import com.swpp2023.eatandtell.dto.TopTag
import com.swpp2023.eatandtell.dto.UploadPostRequest
import com.swpp2023.eatandtell.dto.UserDTO
import com.swpp2023.eatandtell.dto.toggleFollowResponse
import com.swpp2023.eatandtell.dto.toggleLikeResponse
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
    suspend fun deletePost( authorization: String, post_id: Int): Result<Unit> = runCatching{
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

    suspend fun getPersonalizedPosts(authorization: String): Result<GetAllPostsResponse> = runCatching {
        apiService.getPersonalizedPosts(authorization)
    }

    suspend fun getFollowingPosts(authorization: String): Result<GetAllPostsResponse> = runCatching {
        apiService.getFollwingPosts(authorization)
    }

    suspend fun getFollowers(authorization: String, userid: Int?):Result<List<UserDTO>> = runCatching{
        apiService.getFollowers(authorization,userid)
    }

    suspend fun getFollowings(authorization: String, userid: Int?):Result<List<UserDTO>> = runCatching{
        apiService.getFollowings(authorization,userid)
    }


}
