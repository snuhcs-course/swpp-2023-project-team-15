package com.example.eatandtell.ui.start
import RetrofitClient
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatandtell.SharedPreferencesManager
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
                Log.d("login", "success")
                if(token!=null) {
                    SharedPreferencesManager.setToken(context, token)
                }
                onSuccess(token)
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Network error"
                Log.d("login", "error$errorMessage")
                showToast(context, "로그인에 실패하였습니다")
            }
        }
    }

    fun registerUser(username: String, password: String, email: String, context: Context, onSuccess: (String?) -> Unit) {
        val registrationData = RegisterRequest(username, password, email)

        viewModelScope.launch {
            try {
                val response = apiService.registerUser(registrationData)
                val token = response?.token
                Log.d("sign up", "success")
                if(token!=null) SharedPreferencesManager.setToken(context, token)
                onSuccess(token)
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Network error"
                Log.d("sign up", "error$errorMessage")
                showToast(context, "회원가입에 실패하였습니다")
            }
        }
    }


}

