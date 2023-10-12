package com.example.eatandtell.ui.uploadpost
import RetrofitClient
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.eatandtell.di.ApiService
import com.example.eatandtell.dto.LoginRequest
import com.example.eatandtell.dto.LoginResponse
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.UploadPostRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadViewModel : ViewModel() {
    //interface for callback
    interface UploadCallback {
        fun onUploadSuccess()
        fun onUploadError(errorMessage: String)
    }

    private val apiService = RetrofitClient.retro.create(ApiService::class.java)
    fun uploadPost(postData: UploadPostRequest,  callback: UploadCallback) {
        val call = apiService.uploadPost(postData)

        call.enqueue(object : Callback<PostDTO> {
            override fun onResponse(call: Call<PostDTO>, response: Response<PostDTO>) {
                if (response.isSuccessful) {
                    callback.onUploadSuccess()
                } else {
                    val errorMessage = response.message()
                    Log.d("upload post error", ""+response.code()+errorMessage)
                    callback.onUploadError("Upload post failed: $errorMessage")
                }
            }

            override fun onFailure(call: Call<PostDTO>, t: Throwable) {
                val errorMessage = t.message ?: "Network error"
                callback.onUploadError(errorMessage)
            }
        })
    }


}

