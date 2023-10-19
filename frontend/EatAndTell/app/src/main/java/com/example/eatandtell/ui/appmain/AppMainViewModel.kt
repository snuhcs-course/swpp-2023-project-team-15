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
            val imageUrl = getImageURL(fileToUpload, context)
            photoUrls.add(imageUrl)
        }
        try {
            val photos = photoUrls.map { PhotoReqDTO(it) }
            val postData = UploadPostRequest(restaurant = restaurant, photos = photos, rating = rating, description = description)
            this.uploadPost(postData, context)
        } catch (e: Exception) {
            // Handle exceptions, e.g., from network calls, here
            Log.d("upload photos and post error", e.message ?: "Network error")
            showToast(context, "포스트 업로드에 실패했습니다")
        }
    }

    private suspend fun uploadPost(postData: UploadPostRequest, context: Context) {
        val authorization = "Token $token"

        try {
            apiService.uploadPost(authorization, postData)
            showToast(context, "포스트가 업로드되었습니다")
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Network error"
            Log.d("upload post error", errorMessage)
            throw e // rethrow the exception to be caught in the calling function
        }
    }

    private suspend fun getImageURL(fileToUpload: MultipartBody.Part?, context: Context): String {
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

    suspend fun getAllPosts(context: Context, onSuccess: (List<PostDTO>) -> Unit) {
        val authorization = "Token $token"
        try {
            val response = apiService.getAllPosts(authorization)
            onSuccess(response.data)
        } catch (e: Exception) {
            throw e // rethrow the exception to be caught in the calling function
        }
    }

    suspend fun getMyPosts(context: Context, onSuccess: (List<PostDTO>) -> Unit) {
        val authorization = "Token $token"
        try {
            val response = apiService.getMyPosts(authorization)
            onSuccess(response.data)
        } catch (e: Exception) {
            throw e // rethrow the exception to be caught in the calling function
        }
    }

}
