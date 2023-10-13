
package com.example.eatandtell.ui.home
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
//import com.example.eatandtell.ui.AppNavigation
import com.example.eatandtell.ui.navigationBar.Navigation
import com.example.eatandtell.ui.HeartEmpty
import com.example.eatandtell.ui.HeartFull
import com.example.eatandtell.ui.navigationBar.NavigationBar
import com.example.eatandtell.ui.PostImage
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.StarRating
import com.example.eatandtell.ui.theme.Black
import com.example.eatandtell.ui.theme.MainColor

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                HomeScreen()

                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            onHomeClick = { navController.navigate("home") },
                            onSearchClick = { navController.navigate("search") },
                            onPlusClick = { navController.navigate("upload") },
                            onProfileClick = { navController.navigate("profile") },
                            profileUrl = "https://newprofilepic.photo-cdn.net//assets/images/article/profile.jpg?90af0c8"
                        )
                    }
                ) { innerPadding ->
                   Navigation(navController = navController, Modifier.padding(innerPadding))
                }
            }
        }
    }
}
//TODO: parameter PostDTO로 바꾸기

    @Composable
    fun Post(
        profileUrl: String,
        username: String,
        userDescription: String,
        restaurantName: String,
        rating: Float,
        imageUrls: List<String>,
        restaurantDescription: String,
        isLiked: Boolean,
        likes: Int,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Profile Row
            Spacer(modifier = Modifier.height(8.dp))
            Profile(profileUrl, username, userDescription);

            Spacer(modifier = Modifier.height(11.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                //식당 이름
                Text(
                    text = restaurantName, style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight(700),
                        color = Black,
                    ), modifier = Modifier
                        .width(236.dp)
                        .height(20.dp),
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(4.dp))

                //ratings
                Row(
                    horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .width(120.dp)
                        .height(16.dp)
                ) {
                    StarRating(rating)
                    // 다른 child views 추가
                }
            }

            Spacer(modifier = Modifier.height(7.dp))

            // Images Row
            Row(
                modifier = Modifier
                    .height(160.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                for (imageUrl in imageUrls) {
                    PostImage(imageUrl)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Restaurant Description
            Text(
                text = restaurantDescription, style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFF262626),
                ), modifier = Modifier
                    .width(320.dp)
                    .height(54.dp),
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = likes.toString(),
                    style = TextStyle(
                        fontSize = 11.sp,
                        lineHeight = 16.5.sp,
                        fontWeight = FontWeight(500),
                        color = MainColor,
                    ),
                    modifier = Modifier
                        .width(16.dp)
                )
                if (isLiked) HeartFull() else HeartEmpty()
            }
        }
    }

    @Composable
    fun HomeScreen() {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
        ) {
            repeat(5) {
                Post(
                    profileUrl = "https://newprofilepic.photo-cdn.net//assets/images/article/profile.jpg?90af0c8",
                    username = "Joshua-i",
                    userDescription = "고독한 미식가",
                    restaurantName = "포케앤 샐러드",
                    rating = 3.5f,
                    imageUrls = listOf(
                        "https://api.nudge-community.com/attachments/339560",
                        "https://img.siksinhot.com/place/1650516612762055.jpg?w=560&h=448&c=Y",
                        "https://img1.daumcdn.net/thumb/R800x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FdKS0uX%2FbtrScbvc9HH%2F5I2m53vgz0LWvszHQ9PQNk%2Fimg.jpg"
                    ),
                    restaurantDescription = "정직한 가격에 맛도 있고, 대만족합니다. 매장이 큰편은 아니지만 서빙하시는 분도 친절하시고 양도 배부르네요... 어쩌구저쩌구",
                    isLiked = false,
                    likes = 36,
                )
            }
        }
    }

    @Preview
    @Composable
    fun HomeScreenPreview() {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreen()
        }
    }

