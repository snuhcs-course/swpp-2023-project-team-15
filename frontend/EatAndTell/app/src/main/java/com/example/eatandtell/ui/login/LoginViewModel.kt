package com.example.eatandtell.ui.login
import RetrofitClient
import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import com.example.eatandtell.R
import com.example.eatandtell.di.ApiService
import com.example.eatandtell.dto.LoginRequest
import com.example.eatandtell.dto.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    //interface for callback
    interface LoginCallback {
        fun onLoginSuccess(token: String?)
        fun onLoginError(errorMessage: String)
    }

    private val apiService = RetrofitClient.retro.create(ApiService::class.java)
    fun loginUser(username: String, password: String,  callback: LoginCallback) {
        val loginData = LoginRequest(username, password)
        val call = apiService.loginUser(loginData)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.token
                    callback.onLoginSuccess(token)
                } else {
                    val errorMessage = response.message()
                    Log.d("login error", ""+response.code()+errorMessage)
                    callback.onLoginError("Log in failed: $errorMessage")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                val errorMessage = t.message ?: "Network error"
                callback.onLoginError(errorMessage)
            }
        })
    }


}

