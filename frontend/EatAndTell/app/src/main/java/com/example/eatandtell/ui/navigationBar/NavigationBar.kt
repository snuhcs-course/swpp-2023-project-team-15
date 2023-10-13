package com.example.eatandtell.ui.navigationBar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eatandtell.ui.ClickableProfileImage
import com.example.eatandtell.ui.Home
import com.example.eatandtell.ui.PlusCircle
import com.example.eatandtell.ui.SearchRefraction
import com.example.eatandtell.ui.home.HomeScreen

@Composable
fun NavigationBar(
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onPlusClick: () -> Unit,
    onProfileClick: () -> Unit,
    profileUrl: String
    ) {
    Surface {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            Home(onClick = onHomeClick)
            // Search
            SearchRefraction(onClick = onSearchClick)
            // Plus (Upload)
            PlusCircle(onClick = onPlusClick)
            // Profile
            ClickableProfileImage(profileUrl, onClick = onProfileClick)

        }
    }
}


@Composable
fun Navigation(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable(route = "home") {
            HomeScreen()
        }
        composable(route = "search") {
            SearchScreen(navController)
        }
        composable(route = "upload") {
            UploadScreen(navController)

        }
        composable(route = "profile") {
            ProfileScreen(navController)
        }
    }
}

//Delete when done Implementing Each Screens
@Composable
fun SearchScreen(navController: NavHostController) {
     Text(text = "Search Screen")
}
@Composable
fun UploadScreen(navController: NavHostController) {      Text(text = "Upload Screen")
}
@Composable
fun ProfileScreen(navController: NavHostController) {      Text(text = "Profile Screen")
}

@Preview
@Composable
fun PreviewNavigationBar() {
    Surface {
        NavigationBar(
            profileUrl = "https://avatars.githubusercontent.com/u/44080404?v=4",
            onHomeClick = { /*TODO: Home Clicked*/ },
            onSearchClick = { /*TODO: Search Clicked*/ },
            onPlusClick = { /*TODO: Plus Clicked*/ },
            onProfileClick = { /*TODO: Profile Clicked*/ },
        )
    }
}



