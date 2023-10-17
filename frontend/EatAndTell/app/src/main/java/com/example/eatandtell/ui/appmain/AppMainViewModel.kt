package com.example.eatandtell.ui.appmain
import RetrofitClient
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eatandtell.di.ApiService
import com.example.eatandtell.dto.ImageURLResponse
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.UploadPostRequest
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    fun uploadPost(postData: UploadPostRequest,  callback: UploadCallback) {
        val authorization = "Token $token"
        val call = apiService.uploadPost(authorization, postData)

        call.enqueue(object : Callback<PostDTO> {
            override fun onResponse(call: Call<PostDTO>, response: Response<PostDTO>) {
                if (response.isSuccessful) {
                    callback.onUploadSuccess()
                } else {
                    val errorMessage = response.message()
                    Log.d("upload post error", ""+response.code()+" error message is "+errorMessage)
                    callback.onUploadError("Upload post failed: $errorMessage")
                }
            }

            override fun onFailure(call: Call<PostDTO>, t: Throwable) {
                val errorMessage = t.message ?: "Network error"
                callback.onUploadError(errorMessage)
            }
        })
    }

    fun getImageURL(fileToUpload: MultipartBody.Part?, callback: ImageCallback) {
        val authorization = "Token $token"
        val call = apiService.getImageURL(authorization, fileToUpload)

        call.enqueue(object : Callback<ImageURLResponse> {
            override fun onResponse(call: Call<ImageURLResponse>, response: Response<ImageURLResponse>) {
                if (response.isSuccessful) {
                    callback.onImageSuccess(response.body()?.image_url)
                } else {
                    val errorMessage = response.message()
                    callback.onImageError(""+response.code()+" error message is "+errorMessage)
                }
            }

            override fun onFailure(call: Call<ImageURLResponse>, t: Throwable) {
                Log.d("getting image url on Failure", "")
                val errorMessage = t.message ?: "Network error"
                callback.onImageError(errorMessage)
            }
        })
    }


}
