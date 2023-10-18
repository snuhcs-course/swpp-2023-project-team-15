package com.example.eatandtell.ui.appmain
import RetrofitClient
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatandtell.di.ApiService
import com.example.eatandtell.dto.PhotoReqDTO
import com.example.eatandtell.dto.RestReqDTO
import com.example.eatandtell.dto.UploadPostRequest
import com.example.eatandtell.ui.showToast
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

        val deferredImageUrls = mutableListOf<Deferred<String>>()
        val photoByteArrays = photoPaths.mapNotNull { prepareFileData(it) }
        for(byteArray in photoByteArrays) {
            //change photoPath in to photo with formData type
            val requestBody: RequestBody = byteArray.toRequestBody("image/*".toMediaTypeOrNull())
            val fileToUpload: MultipartBody.Part = MultipartBody.Part.createFormData("image", "this_name_does_not_matter.jpg", requestBody)
            //get photo url from server
            val deferredImageUrl = viewModelScope.async {
                getImageURL(fileToUpload, context)
            }
            deferredImageUrls.add(deferredImageUrl)
        }
        try {
            val imageUrls = deferredImageUrls.awaitAll() // This will suspend until all uploads are done
            val photoUrls = imageUrls.toMutableList() // Assuming photoUrls is a MutableList<String>

            // Proceed with the post upload
            if (photoByteArrays.isNotEmpty() && photoUrls.isEmpty()) {
                showToast(context, "photo Url이 없어 업로드에 실패했습니다.")
            } else {
                val photos = photoUrls.map { PhotoReqDTO(it) }
                val postData = UploadPostRequest(restaurant = restaurant, photos = photos, rating = rating, description = description)
                this.uploadPost(postData, context)
            }
        } catch (e: Exception) {
            // Handle exceptions, e.g., from network calls, here
            showToast(context, "An error occurred: ${e.message}")
        }
    }

    private suspend fun uploadPost(postData: UploadPostRequest, context: Context) {
        val authorization = "Token $token"

        try {
            val response = apiService.uploadPost(authorization, postData)
            showToast(context, "Upload post success")
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Network error"
            Log.d("upload post error", errorMessage)
            showToast(context, "Upload post failed $errorMessage")
        }
    }

    private suspend fun getImageURL(fileToUpload: MultipartBody.Part?, context: Context): String {
        val authorization = "Token $token"

        try {
            val response = apiService.getImageURL(authorization, fileToUpload) // Assuming this is a suspend function call
            val imageUrl = response.image_url
            showToast(context, "Get image url success")
            return imageUrl
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Network error"
            Log.d("get image url error", errorMessage)
            showToast(context, "Get image url failed $errorMessage")
            throw e // rethrow the exception to be caught in the calling function
        }
    }
}
