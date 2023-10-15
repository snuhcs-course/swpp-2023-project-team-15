package com.example.eatandtell.ui.appmain
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.RestaurantDTO
import com.example.eatandtell.dto.UserDTO
import com.example.eatandtell.ui.HeartEmpty
import com.example.eatandtell.ui.HeartFull
import com.example.eatandtell.ui.MediumRedButton
import com.example.eatandtell.ui.PostImage
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.StarRating
import com.example.eatandtell.ui.Tag
import com.example.eatandtell.ui.theme.Black
import com.example.eatandtell.ui.theme.Inter
import com.example.eatandtell.ui.theme.MainColor

import com.google.accompanist.flowlayout.FlowRow



@Composable
fun ProfileRow(profileUrl: String, username: String, userDescription: String, followings: Int,
               followers: Int, onClick: () -> Unit,tags: List<String>) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Profile(profileUrl, username, userDescription)
            MediumRedButton(onClick = onClick, text = "팔로우하기")
        }
        Spacer(modifier = Modifier.height(11.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "$followings Followings" ,
                 style = TextStyle(
                     fontSize = 16.sp,
                     lineHeight = 18.sp,
                     fontFamily = Inter,
                     fontWeight = FontWeight(500),
                        color = Color.Black,
                     )
            )
            Text(text = "$followers Followers",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 18.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight(500),
                    color = Color.Black,
                )
            )
        }
        Spacer(modifier = Modifier.height(11.dp))

        FlowRow(
            modifier = Modifier
                .fillMaxWidth(),
            mainAxisSpacing = 8.dp,  // adjust as needed
            crossAxisSpacing = 8.dp  // adjust as needed
        ) {
            tags.forEach { tagName ->
                Tag(tagName)
            }
        }
        Spacer(modifier = Modifier.height(15.dp))

    }
}

@Preview
@Composable
fun ProfileRowPreview() {
    Surface{
        ProfileRow(
            profileUrl = "https://newprofilepic.photo-cdn.net//assets/images/article/profile.jpg?90af0c8",
            username = "joshua-i",
            userDescription = "고독한 미식가",
            followings = 10,
            followers = 20,
            onClick = {},
            tags = listOf("#육식주의자", "#미식가", "#리뷰왕","#감성","#한식")
        )
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),) {
        ProfileRow(
            profileUrl = "https://newprofilepic.photo-cdn.net//assets/images/article/profile.jpg?90af0c8",
            username = "joshua-i",
            userDescription = "고독한 미식가",
            followings = 10,
            followers = 20,
            onClick = {},
            tags = listOf("#육식주의자", "#미식가", "#리뷰왕","#감성","#한식")
        )
        repeat(5) {
            Post(
                restaurantName = "포케앤 샐러드",
                rating = "3.5",
                imageUrls = listOf(
                    "https://api.nudge-community.com/attachments/339560",
                    "https://img.siksinhot.com/place/1650516612762055.jpg?w=560&h=448&c=Y",
                    "https://img1.daumcdn.net/thumb/R800x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FdKS0uX%2FbtrScbvc9HH%2F5I2m53vgz0LWvszHQ9PQNk%2Fimg.jpg"
                ),
                restaurantDescription = "정직한 가격에 맛도 있고, 대만족합니다. 매장이 큰편은 아니지만 서빙하시는 분도 친절하시고 양도 배부르네요... 어쩌구저쩌구",
                isLiked = false,
                likes = 36,
            )
//            Spacer(modifier = Modifier.height(15.dp)) // space between posts
            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

        }

        // navigation bottom app bar 때문에 스크롤이 가려지는 것 방지 + 20.dp padding
        Spacer(modifier = Modifier.height(70.dp))
    }
}






@Preview
@Composable
fun ProfileScreenPreview() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        ProfileScreen()
    }
}
