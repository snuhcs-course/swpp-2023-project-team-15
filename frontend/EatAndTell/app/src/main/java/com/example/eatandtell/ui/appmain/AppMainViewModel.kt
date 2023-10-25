package com.example.eatandtell.ui.appmain
import RetrofitClient
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatandtell.di.ApiService
import com.example.eatandtell.dto.PhotoReqDTO
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.RegisterRequest
import com.example.eatandtell.dto.RestReqDTO
import com.example.eatandtell.dto.UploadPostRequest
import com.example.eatandtell.dto.UserDTO
import com.example.eatandtell.dto.UserInfoDTO
import com.example.eatandtell.ui.showToast
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AppMainViewModel() : ViewModel() {

    private var token: String? = null
    fun initialize(token: String?) {
        this.token = token
    }

    private val apiService = RetrofitClient.retro.create(ApiService::class.java)

    suspend fun uploadPhotosAndPost(photoPaths: List<Uri>,
                                    restaurant : RestReqDTO,
                                    rating: String,
                                    description: String,
                                    context: Context
                                    ) {
        fun prepareFileData(photoPath: Uri): ByteArray? {
            val contentResolver = context.contentResolver
            contentResolver.openInputStream(photoPath)?.use { inputStream ->
                return inputStream.readBytes()
            }
            return null
        }

        val photoUrls = mutableListOf<String>()
        val photoByteArrays = photoPaths.mapNotNull { prepareFileData(it) }
        for(byteArray in photoByteArrays) {
            val requestBody: RequestBody = byteArray.toRequestBody("image/*".toMediaTypeOrNull())
            val fileToUpload: MultipartBody.Part = MultipartBody.Part.createFormData("image", "this_name_does_not_matter.jpg", requestBody)
            val imageUrl = getImageURL(fileToUpload)
            photoUrls.add(imageUrl)
        }
        try {
            Log.d("upload photos and post",  "trying")
            val photos = photoUrls.map { PhotoReqDTO(it) }
            val postData = UploadPostRequest(restaurant = restaurant, photos = photos, rating = rating, description = description)
            this.uploadPost(postData)
            showToast(context, "포스트가 업로드되었습니다")
        } catch (e: Exception) {
            // Handle exceptions, e.g., from network calls, here
            Log.d("upload photos and post error", e.message ?: "Network error")
            showToast(context, "포스트 업로드에 실패했습니다")
        }
    }

    private suspend fun uploadPost(postData: UploadPostRequest) {
        val authorization = "Token $token"

        try {
            apiService.uploadPost(authorization, postData)
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Network error"
            Log.d("upload post error", errorMessage)
            throw e // rethrow the exception to be caught in the calling function
        }
    }

    private suspend fun getImageURL(fileToUpload: MultipartBody.Part?): String {
        val authorization = "Token $token"

        try {
            val response = apiService.getImageURL(authorization, fileToUpload) // Assuming this is a suspend function call
            val imageUrl = response.image_url
            return imageUrl
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Network error"
            Log.d("get image url error", errorMessage)
            throw e // rethrow the exception to be caught in the calling function
        }
    }

    suspend fun getAllPosts(onSuccess: (List<PostDTO>) -> Unit) {
        val authorization = "Token $token"
        try {
            val response = apiService.getAllPosts(authorization)
            onSuccess(response.data)
        } catch (e: Exception) {
            throw e // rethrow the exception to be caught in the calling function
        }
    }


    suspend fun getUserFeed(userId: Int? = null, onSuccess: (UserInfoDTO, List<PostDTO>) -> Unit) {
        val authorization = "Token $token"
        try {
            val response = (
                if (userId != null) apiService.getUserFeed(authorization, userId)
                 else apiService.getMyFeed(authorization)
            )
            // TODO: listOf() -> response.tags, response.is_following 구현 시 잘 되는지 확인
            println("user feed response is")
            println(response)
            val myInfo = UserInfoDTO(
                id = response.id,
                username = response.username,
                description = response.description,
                avatar_url = response.avatar_url,
                tags = response.tags,
                is_followed = response.is_followed,
                follower_count = response.follower_count,
                following_count = response.following_count,
            )
            val myPosts = response.posts?: listOf() //posts가 null이라서 임시처리
            println("get user feed success")
            onSuccess(myInfo, myPosts)
        } catch (e: Exception) {
            print("get user feed error")
            println(e.message ?: "Network error")
            throw e // rethrow the exception to be caught in the calling function
        }
    }



    suspend fun toggleLike(post_id: Int) {
        val authorization = "Token $token"
        try {
            val response = apiService.toggleLike(authorization, post_id)
            Log.d("toggle like", "success")
        } catch (e: Exception) {
            Log.d("toggle like error", e.message ?: "Network error")
        }
    }

    suspend fun getMyProfile(onSuccess: (UserDTO) -> Unit){
        val authorization = "Token $token"
        try {
            val response = apiService.getMyFeed(authorization)
            val myInfo = UserDTO(response.id, response.username, response.description, response.avatar_url, listOf())
            Log.d("getMyInfo", "success")
            onSuccess(myInfo)
        } catch (e: Exception) {
            Log.d("getMyInfo error", e.message ?: "Network error")
            throw e // rethrow the exception to be caught in the calling function
        }
    }

    suspend fun getFilteredUsersByName(username: String, onSuccess: (List<UserDTO>) -> Unit) {
        val authorization = "Token $token"
        try {
            val response = apiService.getFilteredUsersByName(authorization, username)
            //print response
            println(response)
            onSuccess(response)
            Log.d("getFilteredUsersByName", "success")
        } catch (e: Exception) {
            Log.d("getFilteredUsersByName error", e.message ?: "Network error")
            throw e // rethrow the exception to be caught in the calling function
        }
    }

    suspend fun getFilteredUsersByTag(tag: String, onSuccess: (List<UserDTO>) -> Unit) {
        val authorization = "Token $token"
        try {
            val response = apiService.getFilteredUsersByTag(authorization, tag)
            onSuccess(response)
            Log.d("getFilteredUsersByTag", "success")
        } catch (e: Exception) {
            Log.d("getFilteredUsersByTag error", e.message ?: "Network error")
            throw e // rethrow the exception to be caught in the calling function
        }
    }

    suspend fun getFilteredByRestaurants(restaurantName: String, onSuccess: (List<PostDTO>) -> Unit) {
        val authorization = "Token $token"
        try {
            val response = apiService.getFilteredByRestaurants(authorization, restaurantName)
            onSuccess(response.data)
            Log.d("getFilteredByRestaurants", "success")
        } catch (e: Exception) {
            Log.d("getFilteredByRestaurants error", e.message ?: "Network error")
            throw e // rethrow the exception to be caught in the calling function
        }
    }
}
