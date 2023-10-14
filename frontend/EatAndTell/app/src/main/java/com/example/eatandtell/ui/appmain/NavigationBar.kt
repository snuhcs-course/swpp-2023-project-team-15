package com.example.eatandtell.ui.appmain

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eatandtell.ui.ClickableProfileImage
import com.example.eatandtell.ui.Home
import com.example.eatandtell.ui.PlusCircle
import com.example.eatandtell.ui.SearchRefraction
import com.example.eatandtell.ui.theme.Black
import com.example.eatandtell.ui.theme.EatAndTellTheme

//TODO: 스크린이 전체가 다 안 참
@Composable
fun BottomNavBar(
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
            verticalAlignment = Alignment.CenterVertically,
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
fun BottomNav(navController: NavHostController, modifier: Modifier, context: ComponentActivity, viewModel: AppMainViewModel) {
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
            UploadScreen(navController, context, viewModel)

        }
        composable(route = "profile") {
            ProfileScreen(navController)
        }
    }
}


fun navigateToDestination(navController: NavHostController, destination: String) {
    navController.navigate(destination) {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true // Preserve state
        }
        // Avoid multiple copies of the same destination when re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        restoreState = true
    }
}

//Delete when done Implementing Each Screens
@Composable
fun SearchScreen(navController: NavHostController) {
     Text(text = "Search Screen")
}

@Composable
fun ProfileScreen(navController: NavHostController) {      Text(text = "Profile Screen")
}

@Preview
@Composable
fun PreviewNavigationBar() {
    Surface {
        BottomNavBar(
            profileUrl = "https://avatars.githubusercontent.com/u/44080404?v=4",
            onHomeClick = { /*TODO: Home Clicked*/ },
            onSearchClick = { /*TODO: Search Clicked*/ },
            onPlusClick = { /*TODO: Plus Clicked*/ },
            onProfileClick = { /*TODO: Profile Clicked*/ },
        )
    }
}


// Top Bar
@Composable
fun BackBar(currentScreenName: String, navigateUp: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
            IconButton(onClick = {
                Log.d("BackBar", "Back Button Clicked")
                navigateUp()
            }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "Back Button"
                )
            }
        Text(
            text = currentScreenName,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
            ),
        )

    }
}

@Preview(showBackground = true)
@Composable
fun BackBarPreview() {
    EatAndTellTheme {
        BackBar(currentScreenName = "리뷰 작성", navigateUp = {})
    }
}


