package com.example.eatandtell.ui.login
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)

interface ApiService {
    @POST("login/") // The login endpoint (hypothetical)
    fun loginUser(@Body loginData: LoginRequest): Call<LoginResponse>
}


class LoginViewModel : ViewModel() {
    //interface for callback
    interface LoginCallback {
        fun onLoginSuccess(token: String?)
        fun onLoginError(errorMessage: String)
    }

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    val retrofit = Retrofit.Builder()
        .baseUrl("http://ec2-13-125-91-166.ap-northeast-2.compute.amazonaws.com/users/") // actual backend URL
        .addConverterFactory(MoshiConverterFactory.create()) // Add coroutine adapter
        .build()

    private val apiService = retrofit.create(ApiService::class.java)
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

    //Sealed Class (closer to mvvm)
    sealed class LoginResult {
        data class Success(val token: String?) : LoginResult()
        data class Error(val errorMessage: String) : LoginResult()
    }

}

