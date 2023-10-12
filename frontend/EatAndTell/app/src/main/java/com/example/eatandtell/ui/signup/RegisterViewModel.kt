package com.example.eatandtell.ui.signup
import RetrofitClient
import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import com.example.eatandtell.R
import com.example.eatandtell.di.ApiService
import com.example.eatandtell.dto.RegisterRequest
import com.example.eatandtell.dto.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    //interface for callback
    interface RegisterCallback {
        fun onRegisterSuccess(token: String?)
        fun onRegisterError(errorMessage: String)
    }

    private val apiService = RetrofitClient.retro.create(ApiService::class.java)



    fun registerUser(username: String, password: String, email: String, callback: RegisterCallback) {
        val registrationData = RegisterRequest(username, password, email)
        val call = apiService.registerUser(registrationData)

        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val registrationResponse = response.body()
                    val token = registrationResponse?.token
                    callback.onRegisterSuccess(token)
                } else {
                    val errorMessage = response.message()
                    Log.d("register error", ""+response.code()+errorMessage)
                    callback.onRegisterError("Registration failed: $errorMessage")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                val errorMessage = t.message ?: "Network error"
                callback.onRegisterError(errorMessage)
            }
        })
    }

}

