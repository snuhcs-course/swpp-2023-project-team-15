package com.swpp2023.eatandtell.data.repository

import android.content.Context
import com.swpp2023.eatandtell.data.security.SharedPreferencesManager
import javax.inject.Inject


class TokenRepository@Inject constructor(private val sharedPreferencesManager: SharedPreferencesManager) {
    fun saveToken(context: Context, token: String) {
        sharedPreferencesManager.setToken(context,token)
    }

    fun getToken(context:Context): Map<String,String?> {
        return sharedPreferencesManager.getToken(context)
    }
}