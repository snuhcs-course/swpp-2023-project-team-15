package com.example.eatandtell.ui.appmain

import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.UserInfoDTO
import com.example.eatandtell.ui.MediumRedButton
import com.example.eatandtell.ui.MediumWhiteButton
import com.example.eatandtell.ui.Post
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.Tag
import com.example.eatandtell.ui.showToast
import com.example.eatandtell.ui.theme.Inter
import com.example.eatandtell.ui.theme.MainColor
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch


@Composable
fun ProfileRow(viewModel: AppMainViewModel, userInfo: UserInfoDTO, onClick: () -> Unit, buttonText: String, itsMe : Boolean = false, context : ComponentActivity? = null) {
    var tags by remember { mutableStateOf(userInfo.tags) }

    println("itsMe : $itsMe")

    Column {
        //Profile and follow button
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Profile(userInfo.avatar_url, userInfo.username, userInfo.description)
            if (itsMe || buttonText == "팔로우하기") MediumRedButton(onClick = { onClick() }, text = buttonText)
            else MediumWhiteButton(onClick = { onClick() }, text = buttonText)
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
        Row (
            modifier = Modifier
                .fillMaxWidth(),
        ) {

            if(tags.isEmpty()) {
                //show Text to refresh tags
                Text(text = "아직 태그가 없습니다",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight(500),
                        color = Color.Gray,
                    ), modifier = Modifier
                        .weight(1f),
                )
            }
            else {
                FlowRow(
                    modifier = Modifier
                        .weight(1f),
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    println("tags: ${userInfo.tags}")
                    userInfo.tags.forEach { tagName ->
                        Tag(tagName)
                    }
                }
            }

            //refresh button
            if (itsMe) Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh Icon",
                tint = MainColor,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = {
                        viewModel.refreshTags(
                            onSuccess = { it ->
                                tags = it
                                println("refreshed tags: $tags")
                            },
                            context = context!!
                        )
                    })
                    .align(Alignment.CenterVertically)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))
    }

}


@Composable
fun ProfileScreen(context: ComponentActivity, viewModel: AppMainViewModel, navController: NavHostController, userId: Int? = null ) {
    var userPosts by remember { mutableStateOf(emptyList<PostDTO>()) }
    var userInfo by remember { mutableStateOf(UserInfoDTO(0, "", "", "", listOf(), false,0, 0)) }
    var loading by remember { mutableStateOf(true) }
    val coroutinescope = rememberCoroutineScope()


    LaunchedEffect(loading) {
        try {
            viewModel.getUserFeed(
                userId = userId,
                onSuccess = { info, posts ->
                    userInfo = info
                    userPosts = posts
                }
            )
            loading = false
        }
        catch (e: Exception) {
            if (e !is CancellationException) { // 유저가 너무 빨리 화면을 옮겨다니는 경우에는 CancellationException이 발생할 수 있지만, 서버 에러가 아니라서 패스
                loading = false
                println("feed load error $e")
                showToast(context, "유저 피드 로딩에 실패하였습니다 $e")
            }
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
                viewModel = viewModel,
                userInfo = userInfo,
                onClick = {
                    if(isCurrentUser) navigateToDestination(navController, "EditProfile")
                    else { /* TODO: toggle follow */ }
                },
                buttonText = if (isCurrentUser) "프로필 편집" else if (userInfo.is_followed) "팔로잉" else "팔로우하기",
                itsMe = isCurrentUser,
                context = context
            )}

            items(userPosts) { post ->
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
            viewModel = AppMainViewModel(),
            userInfo = UserInfoDTO(0, "joshua-i", "본인 프로필입니다~", "https://newprofilepic.photo-cdn.net//assets/images/article/profile.jpg?90af0c8", tags = listOf("육식주의자"), false,10, 20),
            onClick = {},
            buttonText = "팔로우하기"  // New buttonText parameter
        )
    }
}

