package com.example.eatandtell.ui.start
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.eatandtell.SharedPreferencesManager
import com.example.eatandtell.data.api.ApiService
import com.example.eatandtell.data.repository.MainRepository
import com.example.eatandtell.dto.LoginRequest
import com.example.eatandtell.dto.RegisterRequest
import com.example.eatandtell.ui.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {
    suspend fun loginUser(username: String, password: String, context: Context): String? {
        val loginData = LoginRequest(username, password)
        val response = mainRepository.loginUser(loginData)

        return try {

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

