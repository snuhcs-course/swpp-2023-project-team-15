package com.example.eatandtell.ui.start
import RetrofitClient
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatandtell.di.ApiService
import com.example.eatandtell.dto.LoginRequest
import com.example.eatandtell.dto.LoginResponse
import com.example.eatandtell.dto.RegisterRequest
import com.example.eatandtell.dto.RegisterResponse
import com.example.eatandtell.ui.showToast
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await

class StartViewModel : ViewModel() {

    private val apiService = RetrofitClient.retro.create(ApiService::class.java)
    fun loginUser(username: String, password: String, context: Context, onSuccess: (String?) -> Unit) {
        val loginData = LoginRequest(username, password)

        viewModelScope.launch {
            try {
                val response = apiService.loginUser(loginData)
                val token = response?.token
                showToast(context, "Log in successful")
                onSuccess(token)
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Network error"
                showToast(context, "Log in failed $errorMessage")
            }
        }
    }

    fun registerUser(username: String, password: String, email: String, context: Context, onSuccess: (String?) -> Unit) {
        val registrationData = RegisterRequest(username, password, email)

        viewModelScope.launch {
            try {
                val response = apiService.registerUser(registrationData)
                val token = response?.token
                showToast(context, "Sign up successful")
                onSuccess(token)
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Network error"
                showToast(context, "Sign up failed $errorMessage")
            }
        }
    }


}

