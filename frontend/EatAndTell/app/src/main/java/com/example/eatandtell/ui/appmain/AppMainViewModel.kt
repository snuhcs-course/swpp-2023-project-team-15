package com.example.eatandtell.ui.appmain
import RetrofitClient
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatandtell.di.ApiService
import com.example.eatandtell.dto.EditProfileRequest
import com.example.eatandtell.dto.PhotoReqDTO
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.RegisterRequest
import com.example.eatandtell.dto.RestReqDTO
import com.example.eatandtell.dto.SearchedRestDTO
import com.example.eatandtell.dto.UploadPostRequest
import com.example.eatandtell.dto.UserDTO
import com.example.eatandtell.dto.UserInfoDTO
import com.example.eatandtell.ui.showToast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AppMainViewModel() : ViewModel() {

    private var token: String? = null
    var myProfile = UserDTO(0, "", "", "", listOf())

    fun initialize(token: String?) {
        this.token = token
        viewModelScope.launch {
            getMyProfile()
        }
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
            Log.d("upload photos and post",  "success")
            showToast(context, "포스트가 업로드되었습니다")
        } catch (e: Exception) {
            // Handle exceptions, e.g., from network calls, here
            //except cancellation exception
            if (e !is CancellationException) {
                Log.d("upload photos and post error", e.message ?: "Network error")
                showToast(context, "포스트 업로드에 실패했습니다")
            }
            else {
                Log.d("upload photos and post error", "cancellation exception")
                showToast(context, "포스트가 업로드되었습니다")
            }
        }
    }

    suspend fun uploadPhotosAndEditProfile(photoPaths: List<Uri>, //실제로는 length 1짜리
                                    description: String,
                                    context: Context,
                                           org_avatar_url: String,
    ) {

        Log.d("edit profile photoPaths: ", photoPaths.toString())

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
            Log.d("edit profile", description)
            Log.d("edit profile", photoUrls.toString())
            Log.d("edit profile", org_avatar_url)
            val url = if (photoUrls.isEmpty()) org_avatar_url else photoUrls[0]
            val profileData = EditProfileRequest(description = description, avatar_url = url)
            Log.d("edit profile", profileData.toString())
            this.editProfile(profileData)
            showToast(context, "프로필이 편집되었습니다")
            myProfile = UserDTO(myProfile.id, myProfile.username, description, url, myProfile.tags) //프로필 편집 후 myProfile 업데이트
        } catch (e: Exception) {
            // Handle exceptions, e.g., from network calls, here
            if (e !is CancellationException) {
                Log.d("edit profile error", e.message ?: "Network error")
                showToast(context, "프로필 편집에 실패했습니다")
            }
            else {
                Log.d("edit profile error", "cancellation exception")
                showToast(context, "프로필이 편집되었습니다")
            }
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

    private suspend fun editProfile(profileData: EditProfileRequest) {
        val authorization = "Token $token"

        try {
            apiService.editProfile(authorization, profileData)
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Network error"
            Log.d("edit profile error", errorMessage)
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

    suspend fun getLikedFeed(onSuccess: (List<PostDTO>) -> Unit) {
        val authorization = "Token $token"
        try {
            val response = apiService.getLikedFeed(authorization)
            onSuccess(response)
        } catch (e: Exception) {
            throw e // rethrow the exception to be caught in the calling function
        }
    }


    suspend fun getUserFeed(userId: Int? = null, onSuccess: (UserInfoDTO, List<PostDTO>) -> Unit) {
        val authorization = "Token $token"
        try {
            println("feed token $authorization")
            println("feed userId $userId")
            val response = (
                if (userId != null) apiService.getUserFeed(authorization, userId)
                 else apiService.getMyFeed(authorization)
            )
            println("user feed response is")
            println(response.tags)
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

    suspend fun deletePost(post_id: Int) {
        val authorization = "Token $token"
        try {
            val response = apiService.deletePost(authorization, post_id)
            Log.d("delete post", "success")
        } catch (e: Exception) {
            Log.d("delete post error", e.message ?: "Network error")
        }
    }

    private suspend fun getMyProfile(){
        val authorization = "Token $token"
        try {
            val response = apiService.getMyFeed(authorization)
            val myProf = UserDTO(response.id, response.username, response.description, response.avatar_url, listOf())
            val myFoll = listOf(
                response.follower_count,
                response.following_count,
            )
            Log.d("getMyProfile", "success")
            myProfile = myProf
        } catch (e: Exception) {
            Log.d("getMyProfile error", e.message ?: "Network error")
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

    suspend fun getSearchedRest(restaurantName: String, x: String?=null, y: String?=null) : List<SearchedRestDTO> {
        val authorization = "Token $token"
        try {
            val response = apiService.getSearchedRest(authorization, restaurantName, x, y)
            Log.d("search rest", "success")
            return response.data
        } catch (e: Exception) {
            Log.d("search rest error", e.message ?: "Network error")
            return listOf()
            //throw e // rethrow the exception to be caught in the calling function
        }
    }

    suspend fun refreshTags(onSuccess: (List<String>) -> Unit, context: Context) {
        val authorization = "Token $token"
        try {
            val response = apiService.refreshTags(authorization)
            myProfile = UserDTO(myProfile.id, myProfile.username, myProfile.description, myProfile.avatar_url, response.user_tags) //프로필 편집 후 myProfile 업데이트
            onSuccess(response.user_tags)
            Log.d("refresh tags", "success")
            showToast(context, "태그가 업데이트되었습니다")
        } catch (e: Exception) {
            Log.d("refresh tags error", e.message ?: "Network error")
            showToast(context, "태그 업데이트에 실패하였습니다")
        }
    }
}
