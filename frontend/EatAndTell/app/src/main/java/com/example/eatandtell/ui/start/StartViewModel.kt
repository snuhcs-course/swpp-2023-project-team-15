package com.example.eatandtell.ui.start
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.eatandtell.SharedPreferencesManager
import com.example.eatandtell.di.ApiService
import com.example.eatandtell.dto.LoginRequest
import com.example.eatandtell.dto.RegisterRequest
import com.example.eatandtell.ui.showToast

class StartViewModel(private val apiService: ApiService) : ViewModel() {

    //private val apiService = RetrofitClient.retro.create(ApiService::class.java)
    suspend fun loginUser(username: String, password: String, context: Context): String? {
        val loginData = LoginRequest(username, password)
        return try {
            val response = apiService.loginUser(loginData)
            val token = response.token
            Log.d("login", "success")
            SharedPreferencesManager.setToken(context, token)
            token // This will be the return value of the function
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Network error"
            Log.d("login", "error: $errorMessage")
            //showToast(context, "로그인에 실패하였습니다")
            null // In case of an error, return null or you could throw an exception
        }
    }


    suspend fun registerUser(username: String, password: String, email: String, context: Context): String? {
        val registrationData = RegisterRequest(username, password, email)
        return try {
            val response = apiService.registerUser(registrationData)
            val token = response.token
            //Log.d("sign up", "success")
            SharedPreferencesManager.setToken(context, token)
            token
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Network error"
            Log.d("sign up", "error: $errorMessage")
            showToast(context, "회원가입에 실패하였습니다")

            null
        }

    }


}

