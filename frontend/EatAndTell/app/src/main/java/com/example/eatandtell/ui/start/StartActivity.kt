// SignUpScreen.kt
package com.example.eatandtell.ui.start
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eatandtell.ui.theme.White


class StartActivity : ComponentActivity() {
    private val startViewModel: StartViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 20.dp),
                    color = White,
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") { LoginScreen(navController, this@StartActivity, startViewModel) }
                        composable("signup") { SignupScreen(navController, this@StartActivity, startViewModel) }
                        // Add other composables/screens here
                    }
                }
            }
        }
    }
}