
package com.example.eatandtell.ui.appmain
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.eatandtell.ui.start.StartViewModel

//import com.example.eatandtell.ui.AppNavigation

class AppMainActivity : ComponentActivity() {
    private val appMainViewModel: AppMainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                HomeScreen()

                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        BottomNavBar(
                            onHomeClick = { navController.navigate("home") },
                            onSearchClick = { navController.navigate("search") },
                            onPlusClick = { navController.navigate("upload") },
                            onProfileClick = { navController.navigate("profile") },
                            profileUrl = "https://newprofilepic.photo-cdn.net//assets/images/article/profile.jpg?90af0c8"
                        )
                    }
                ) { innerPadding ->
                   BottomNav(navController = navController, modifier = Modifier.padding(innerPadding), this@AppMainActivity, appMainViewModel)
                }
            }
        }
    }
}
