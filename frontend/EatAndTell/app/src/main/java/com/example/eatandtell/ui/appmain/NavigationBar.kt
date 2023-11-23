package com.example.eatandtell.ui.appmain

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.eatandtell.ui.Home
import com.example.eatandtell.ui.MyIcon
import com.example.eatandtell.ui.PlusCircle
import com.example.eatandtell.ui.SearchRefraction
import com.example.eatandtell.ui.theme.Black
import com.example.eatandtell.ui.theme.EatAndTellTheme


@Composable
fun BottomNavBar(
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onPlusClick: () -> Unit,
    onProfileClick: () -> Unit,
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
            //modifier= Modifier.testTag("go_to_home"))
            // Search
            SearchRefraction(onClick = onSearchClick)
            // Plus (Upload)
            PlusCircle(onClick = onPlusClick)
            // Profile
//            ClickableProfileImage(profileUrl, onClick = onProfileClick)
            MyIcon(onClick = onProfileClick)
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
        restoreState = (destination == "Home") // TODO: 이유는 모르겠지만 MyProfile에서는 lazyliststate 복원이 안 돼서 Home에서만 함
    }
}

@Preview
@Composable
fun PreviewNavigationBar() {
    Surface {
        BottomNavBar(
            onHomeClick = { /*Home Clicked*/ },
            onSearchClick = { /*Search Clicked*/ },
            onPlusClick = { /*Plus Clicked*/ },
            onProfileClick = { /*Profile Clicked*/ },
        )
    }
}


// Top Bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(currentScreenName: String, navigateToHome: () -> Unit) {

    val nameText = (
        when (currentScreenName) {
            "Home" -> "홈"
            "Search" -> "검색"
            "SearchRest" -> "맛집 검색"
            "Upload" -> "리뷰 작성"
            "Profile" -> "프로필"
            "EditProfile" -> "프로필 편집"
            "Follower" -> "팔로워"
            "Following"-> "팔로잉"
            else -> "Eat & Tell"
        }
    )

    CenterAlignedTopAppBar(
        title = { Text(
            text = nameText,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                    ),
        ) },
        navigationIcon = { if (currentScreenName != "Home")
            {
                IconButton(onClick = { navigateToHome() }) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowLeft,
                        contentDescription = "go back to home",
                        tint = Black
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    EatAndTellTheme {
        TopBar(currentScreenName = "리뷰 작성", navigateToHome = {})
    }
}


