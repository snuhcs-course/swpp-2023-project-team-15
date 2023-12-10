package com.swpp2023.eatandtell.data.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Log


object SharedPreferencesManager {
    private const val PREFERENCES_NAME = "my_preferences"
    fun getPreferences(mContext: Context): SharedPreferences {
        return mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun clearPreferences(context: Context) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    fun setToken(context: Context, token: String?) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putString("Token", token)
        editor.apply()
        Log.d("shared setting token", "Token: $token")
    }

    fun getToken(context: Context): Map<String, String?> {
        val prefs = getPreferences(context)
        val tokenInfo: MutableMap<String, String?> = HashMap()
        val token = prefs.getString("Token", "")
        tokenInfo["Token"] = token
        return tokenInfo
    }
}