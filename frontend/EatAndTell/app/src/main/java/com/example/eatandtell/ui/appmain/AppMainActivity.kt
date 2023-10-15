
package com.example.eatandtell.ui.appmain
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
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
                val navController = rememberNavController()

                AppMain(appMainViewModel, navController, context = this@AppMainActivity)
            }
        }
    }
}

@Composable
fun AppMain(
    viewModel: AppMainViewModel,
    navController: NavHostController,
    context: ComponentActivity
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreenName = backStackEntry?.destination?.route ?: "Home"

    Scaffold(
        topBar = {
            TopBar(
                currentScreenName = currentScreenName,
                navigateToHome = {
                    navController.popBackStack()
                }
            )
         },
        bottomBar = {
            if(currentScreenName != "Upload")
                BottomNavBar(
                    onHomeClick = { navigateToDestination(navController, "Home")},
                    onSearchClick = { navigateToDestination(navController, "Search") },
                    onPlusClick = { navigateToDestination(navController, "Upload") },
                    onProfileClick = { navigateToDestination(navController, "Profile")},
                    profileUrl = "https://newprofilepic.photo-cdn.net//assets/images/article/profile.jpg?90af0c8"
                )
        }
    ) { innerPadding ->
        BottomNav(navController = navController, modifier = Modifier.padding(innerPadding), context, viewModel)
    }


}
