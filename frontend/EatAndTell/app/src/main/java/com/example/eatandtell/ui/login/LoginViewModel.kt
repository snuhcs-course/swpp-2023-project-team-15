package com.example.eatandtell.ui.login
import RetrofitClient
import RetrofitClient.retrofit
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.eatandtell.ApiService
import com.example.eatandtell.dto.LoginRequest
import com.example.eatandtell.dto.LoginResponse
import com.example.eatandtell.dto.LoginResult
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.converter.moshi.MoshiConverterFactory

class LoginViewModel : ViewModel() {
    //interface for callback
    interface LoginCallback {
        fun onLoginSuccess(token: String?)
        fun onLoginError(errorMessage: String)
    }

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val apiService = RetrofitClient.retrofit.create(ApiService::class.java)
    fun loginUser(username: String, password: String,  callback: LoginCallback) {
        val loginData = LoginRequest(username, password)
        val call = apiService.loginUser(loginData)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.token
                    _loginResult.value = LoginResult.Success(token)
                    callback.onLoginSuccess(token)
                } else {
                    val errorMessage = response.message()
                    _loginResult.value = LoginResult.Error("Log in failed: $errorMessage")
                    callback.onLoginError("Log in failed: $errorMessage")
                    Log.d("MyLog", errorMessage)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                val errorMessage = t.message ?: "Network error"
                callback.onLoginError(errorMessage)
            }
        })
    }


}

