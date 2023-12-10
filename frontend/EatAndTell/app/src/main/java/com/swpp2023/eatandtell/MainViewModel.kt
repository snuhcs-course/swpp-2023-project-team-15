package com.swpp2023.eatandtell

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swpp2023.eatandtell.data.security.SharedPreferencesManager

class MainViewModel : ViewModel() {
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    fun checkIfLoggedIn(context: Context) {
        val token = SharedPreferencesManager.getToken(context)["Token"]
        _isLoggedIn.value = !token.isNullOrEmpty()
    }
}
