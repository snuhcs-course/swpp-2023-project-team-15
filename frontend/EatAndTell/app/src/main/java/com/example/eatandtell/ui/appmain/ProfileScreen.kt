package com.example.eatandtell.ui.appmain
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.RestaurantDTO
import com.example.eatandtell.dto.UserDTO
import com.example.eatandtell.dto.UserInfoDTO
import com.example.eatandtell.ui.HeartEmpty
import com.example.eatandtell.ui.HeartFull
import com.example.eatandtell.ui.MediumRedButton
import com.example.eatandtell.ui.Post
import com.example.eatandtell.ui.PostImage
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.StarRating
import com.example.eatandtell.ui.Tag
import com.example.eatandtell.ui.showToast
import com.example.eatandtell.ui.theme.Black
import com.example.eatandtell.ui.theme.Inter
import com.example.eatandtell.ui.theme.MainColor

import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch


@Composable
fun ProfileRow(userInfo: UserInfoDTO, onClick: () -> Unit,tags: List<String>, buttonText: String) {
    Column {
        //Profile and follow button
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Profile(userInfo.avatar_url, userInfo.username, userInfo.description)
            MediumRedButton(onClick = onClick, text = buttonText)
        }
        Spacer(modifier = Modifier.height(11.dp))

        //Followings and followers
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "${userInfo.follower_count} Followings" ,
                 style = TextStyle(
                     fontSize = 16.sp,
                     lineHeight = 18.sp,
                     fontFamily = Inter,
                     fontWeight = FontWeight(500),
                        color = Color.Black,
                     )
            )
            Text(text = "${userInfo.following_count} Followers",
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

        //Tags
        FlowRow(
            modifier = Modifier
                .fillMaxWidth(),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            tags.forEach { tagName ->
                Tag(tagName)
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
    }

}


@Composable
fun ProfileScreen(context: ComponentActivity, viewModel: AppMainViewModel, navController: NavHostController, userId: Int? = null ) {
    var myPosts by remember { mutableStateOf(emptyList<PostDTO>()) }
    var myInfo by remember { mutableStateOf(UserInfoDTO(0, "", "", "", 0, 0)) }
    var loading by remember { mutableStateOf(true) }
    val coroutinescope = rememberCoroutineScope()


    LaunchedEffect(loading) {
        try {
            if (userId != null) {
                viewModel.getUserProfile(
                    userId = userId,
                    onSuccess = { info, posts ->
                        myInfo = info
                        myPosts = posts
                    }
                )
            } else {
                viewModel.getMyFeed(
                    onSuccess = { info, posts ->
                        myInfo = info
                        myPosts = posts
                    }
                )
            }
            loading = false
        }
        catch (e: Exception) {
            println("my feed load error")
            showToast(context, "피드 로딩에 실패하였습니다")
        }

    }

    if(loading) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                //로딩 화면
                modifier = Modifier
                    .size(70.dp)
            )
        }
    }
    else {
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)) {
            val isCurrentUser = userId == null
            item {ProfileRow(
                userInfo = myInfo,
                onClick = {
                    navigateToDestination(navController, "EditProfile")
                },
                tags = listOf("#육식주의자", "#미식가", "#리뷰왕","#감성","#한식"),
                buttonText = if (isCurrentUser) "프로필 편집" else "팔로우하기"  // New buttonText parameter
            )}

            items(myPosts) { post ->
                Post(post, onHeartClick = {
                    coroutinescope.launch {
                        viewModel.toggleLike(post.id)
                    }
                })
            }
            // navigation bottom app bar 때문에 스크롤이 가려지는 것 방지 + 20.dp padding
            item {Spacer(modifier = Modifier.height(70.dp))}
        }
    }



}


@Preview
@Composable
fun ProfileRowPreview() {
    Surface{
        ProfileRow(
            userInfo = UserInfoDTO(0, "joshua-i", "본인 프로필입니다~", "https://newprofilepic.photo-cdn.net//assets/images/article/profile.jpg?90af0c8", 10, 20),
            onClick = {},
            tags = listOf("#육식주의자", "#미식가", "#리뷰왕","#감성","#한식"),
            buttonText = "팔로우하기"  // New buttonText parameter
        )
    }
}

