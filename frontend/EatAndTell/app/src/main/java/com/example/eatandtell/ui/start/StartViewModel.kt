package com.example.eatandtell.ui.start
import RetrofitClient
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.eatandtell.di.ApiService
import com.example.eatandtell.dto.LoginRequest
import com.example.eatandtell.dto.LoginResponse
import com.example.eatandtell.dto.RegisterRequest
import com.example.eatandtell.dto.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StartViewModel : ViewModel() {
    //interface for callback
    interface LoginCallback {
        fun onLoginSuccess(token: String?)
        fun onLoginError(errorMessage: String)
    }

    interface RegisterCallback {
        fun onRegisterSuccess(token: String?)
        fun onRegisterError(errorMessage: String)
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

