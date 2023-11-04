
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eatandtell.dto.SearchedRestDTO
import com.example.eatandtell.ui.start.StartViewModel

//import com.example.eatandtell.ui.AppNavigation

class AppMainActivity : ComponentActivity() {
    private val appMainViewModel: AppMainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = intent.getStringExtra("Token")
        appMainViewModel.initialize(token)

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
//    val currentScreenName = backStackEntry?.destination?.route ?: "Home"
    val currentScreenName = backStackEntry?.destination?.route?.substringBefore("/{") ?: "Home"

//    val currentScreenName = backStackEntry?.destination?.route?.let {
//        if (it.startsWith("Profile")) "Profile" else it
//    } ?: "Home"
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
                    onPlusClick = { navigateToDestination(navController, "SearchRest") },
                    onProfileClick = { navigateToDestination(navController, "Profile")},
                )
        }
    ) { innerPadding ->
        AppMainNavigate(navController = navController, modifier = Modifier.padding(innerPadding), context, viewModel)
    }
}

@Composable
fun AppMainNavigate(navController: NavHostController, modifier: Modifier, context: ComponentActivity, viewModel: AppMainViewModel) {
    NavHost(
        navController = navController,
        startDestination = "Home"
    ) {
        composable(route = "Home") {
            HomeScreen(context, viewModel, navController)
        }
        composable(route = "Search") {
            SearchScreen(navController,context, viewModel)
        }
//        composable(route = "Upload") {
//            UploadScreen(navController, context, viewModel)
//        }
        composable(
            route = "Upload/{search_id}/{place_name}/{category_name}",
            arguments = listOf(
                navArgument("search_id") {
                    defaultValue = -1
                    type = NavType.IntType
                },
                navArgument("place_name") {
                    defaultValue = ""
                    type = NavType.StringType
                },
                navArgument("category_name") {
                    defaultValue = ""
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val searchId = backStackEntry.arguments?.getInt("search_id")
            val placeName = backStackEntry.arguments?.getString("place_name")
            val categoryName = backStackEntry.arguments?.getString("category_name")
            UploadScreen(navController, context, viewModel, searchId, placeName, categoryName)
        }

        composable(route = "SearchRest") {
            SearchRestScreen(navController, context, viewModel)
        }
        composable(route = "Profile") {
            ProfileScreen(context, viewModel, navController)
        }
        composable(route = "EditProfile") {
            EditProfileScreen(context, viewModel, navController)
        }

        composable(
            route = "Profile/{userId}",
            arguments = listOf(
                navArgument("userId") {
                    defaultValue = "self"  // set default value for self profile
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            ProfileScreen(context, viewModel, navController, userId?.toIntOrNull())
        }

    }
}