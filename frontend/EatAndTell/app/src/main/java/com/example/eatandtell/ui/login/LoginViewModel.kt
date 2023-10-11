package com.example.eatandtell.ui.login
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import com.example.eatandtell.network.ApiClient
import com.example.eatandtell.network.ApiService
import retrofit2.Response


data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)

//Stores LiveData via Sealed Class, accessed from view(activity) via callback
class LoginViewModel : ViewModel() {
    //interface for callback
    interface LoginCallback {
        fun onLoginSuccess(token: String?)
        fun onLoginError(errorMessage: String)
    }

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult



    private val apiService = ApiClient.retrofit.create(ApiService::class.java)
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

