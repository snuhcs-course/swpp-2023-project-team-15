package com.swpp2023.eatandtell.ui.start
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swpp2023.eatandtell.data.repository.ApiRepository
import com.swpp2023.eatandtell.data.repository.TokenRepository
import com.swpp2023.eatandtell.dto.LoginRequest
import com.swpp2023.eatandtell.dto.LoginResponse
import com.swpp2023.eatandtell.dto.RegisterRequest
import com.swpp2023.eatandtell.dto.RegisterResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(private val mainRepository: ApiRepository,private val tokenRepository: TokenRepository) : ViewModel() {
    private val _loginState = mutableStateOf<LoginState>(LoginState.Idle)
    val loginState: State<LoginState> = _loginState
    private val _registerState = mutableStateOf<RegisterState>(RegisterState.Idle)
    val registerState: State<RegisterState> = _registerState

    fun loginUser(username: String, password: String, context: Context){
        viewModelScope.launch {
            val loginData = LoginRequest(username, password)
            val response: Result<LoginResponse> = mainRepository.loginUser(loginData)


            response.onSuccess { response ->
                val token = response.token
                Log.d("login", "success")
                tokenRepository.saveToken(context, token)
                _loginState.value = LoginState.Success(response.token)
            }

            response.onFailure { exception ->
                val errorMessage = exception.message ?: "Network error"
                Log.d("login", "error: $errorMessage")
                _loginState.value = LoginState.Error(exception.localizedMessage ?: "Unknown error")
            }

        }

    }


    fun registerUser(username: String, password: String, email: String, context: Context){
        viewModelScope.launch {
            val registrationData = RegisterRequest(username, password, email)
            val response: Result<RegisterResponse> = mainRepository.registerUser(registrationData)
            response.onSuccess { response ->
                val token = response.token
                Log.d("register", "success")
                tokenRepository.saveToken(context, token)
                _registerState.value = RegisterState.Success(response.token)
            }

            response.onFailure { exception ->
                val errorMessage = exception.message ?: "Network error"
                Log.d("login", "error: $errorMessage")
                _registerState.value = RegisterState.Error(exception.localizedMessage ?: "Unknown error")
            }

        }
    }
    fun resetStates() {
        _loginState.value = LoginState.Idle
        _registerState.value = RegisterState.Idle
    }


}
sealed class LoginState{
    object Idle:LoginState()
    data class Success(val token:String):LoginState()
    data class Error(val message:String):LoginState()
}


sealed class RegisterState{
    object Idle:RegisterState()
    data class Success(val token:String):RegisterState()
    data class Error(val message:String):RegisterState()
}


