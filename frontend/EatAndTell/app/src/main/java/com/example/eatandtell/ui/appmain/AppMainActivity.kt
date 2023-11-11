
package com.example.eatandtell.ui.appmain
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eatandtell.dto.SearchedRestDTO
import com.example.eatandtell.ui.showToast
import com.example.eatandtell.ui.start.StartViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

//import com.example.eatandtell.ui.AppNavigation

class AppMainActivity : ComponentActivity() {
    private val appMainViewModel: AppMainViewModel by viewModels()

    var positionX = ""
    var positionY = ""
    var requestPermissionLauncher : ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {

            Log.d("locationpermission", "asking then granted")
            // Permission is granted. Continue the action or workflow in your
            // app.
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        positionX = location.longitude.toString()
                        positionY = location.latitude.toString()

                    }
                }
        } else {
            //request permission again
            ActivityResultContracts.RequestPermission()

            Log.d("locationpermission", "asking then not granted")
            showToast(this, "위치 권한이 허용되지 않았습니다.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = intent.getStringExtra("Token")
        lifecycleScope.launch {
            appMainViewModel.initialize(token)
        }
        Log.d("locationpermission", "onCreate")

        when {
            ContextCompat.checkSelfPermission(
                this@AppMainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("locationpermission", "already granted")
                // Permission is granted. Continue the action or workflow in your
                // app.
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            positionX = location.longitude.toString()
                            positionY = location.latitude.toString()
                        }
                    }
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }


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
    context: AppMainActivity
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
                    onPlusClick = { navigateToDestination(navController, "Upload") },
                    onProfileClick = { navigateToDestination(navController, "Profile")},
                )
        }
    ) { innerPadding ->
        AppMainNavigate(navController = navController, modifier = Modifier.padding(innerPadding), context, viewModel)
    }
}

@Composable
fun AppMainNavigate(navController: NavHostController, modifier: Modifier, context: AppMainActivity, viewModel: AppMainViewModel) {
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
        composable(route = "Upload") {
            UploadScreen(navController, context, viewModel, null, null, null)
        }
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