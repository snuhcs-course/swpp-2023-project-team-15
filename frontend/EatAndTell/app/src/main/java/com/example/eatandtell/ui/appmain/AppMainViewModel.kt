package com.example.eatandtell.ui.appmain
import RetrofitClient
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eatandtell.di.ApiService
import com.example.eatandtell.dto.ImageURLResponse
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.UploadPostRequest
import com.example.eatandtell.ui.showToast
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await

class AppMainViewModel() : ViewModel() {
    //interface for callback
    interface UploadCallback {
        fun onUploadSuccess()
        fun onUploadError(errorMessage: String)
    }

    interface ImageCallback {
        fun onImageSuccess(imageUrl: String?)
        fun onImageError(errorMessage: String)
    }

    private var token: String? = null
    fun initialize(token: String?) {
        this.token = token
    }

    private val apiService = RetrofitClient.retro.create(ApiService::class.java)

    fun uploadPost(postData: UploadPostRequest, context: Context, onSuccess: () -> Unit) {
        val authorization = "Token $token"

        viewModelScope.launch {
            try {
                val response = apiService.uploadPost(authorization, postData)
                showToast(context, "Upload post success")
                onSuccess()
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Network error"
                Log.d("upload post error", errorMessage)
                showToast(context, "Upload post failed $errorMessage")
            }
        }
    }

    fun getImageURL(fileToUpload: MultipartBody.Part?, context: Context, onSuccess: (String) -> Unit) {
        val authorization = "Token $token"
        //does not want this function to be in coroutine; block the main thread
        try {
            val response = apiService.getImageURL(authorization, fileToUpload)
            showToast(context, "Get image url success")
            onSuccess(response.image_url)
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Network error"
            Log.d("getting image url error", errorMessage)
            showToast(context, "Get image url failed $errorMessage")
        }

    }


}
