package com.example.eatandtell.ui.signup
import RetrofitClient
import RetrofitClient.retrofit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.eatandtell.ApiService
import com.example.eatandtell.dto.RegisterRequest
import com.example.eatandtell.dto.RegisterResponse
import com.example.eatandtell.dto.RegistrationResult
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RegisterViewModel : ViewModel() {
    //interface for callback
    interface RegisterCallback {
        fun onRegisterSuccess(token: String?)
        fun onRegisterError(errorMessage: String)
    }

    private val _registerResult = MutableLiveData<RegistrationResult>()
    val registerResult: LiveData<RegistrationResult> = _registerResult

    private val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

    fun registerUser(username: String, password: String, email: String, callback: RegisterCallback) {
        val registrationData = RegisterRequest(username, password, email)
        val call = apiService.registerUser(registrationData)

        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val registrationResponse = response.body()
                    val token = registrationResponse?.token
                    _registerResult.value = RegistrationResult.Success(token)
                    callback.onRegisterSuccess(token)
                } else {
                    println(""+response.code())
                    val errorMessage = response.message()
                    _registerResult.value = RegistrationResult.Error("Registration failed: $errorMessage")
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

