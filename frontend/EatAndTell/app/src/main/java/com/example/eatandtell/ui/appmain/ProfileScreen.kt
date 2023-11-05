package com.example.eatandtell.ui.appmain
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.eatandtell.ui.MediumWhiteButton
import com.example.eatandtell.ui.Post
import com.example.eatandtell.ui.PostImage
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.StarRating
import com.example.eatandtell.ui.Tag
import com.example.eatandtell.ui.showToast
import com.example.eatandtell.ui.theme.Black
import com.example.eatandtell.ui.theme.Gray
import com.example.eatandtell.ui.theme.Inter
import com.example.eatandtell.ui.theme.MainColor

import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch


@Composable
fun ProfileRow(viewModel: AppMainViewModel, userInfo: UserInfoDTO, onClick: () -> Unit, buttonText: String, itsMe : Boolean = false, context : ComponentActivity? = null) {
    var tags by remember { mutableStateOf(userInfo.tags) } //TODO: tags만 업데이트하지 말고 userInfo를 다시 불러와야 할까?
    val coroutinescope = rememberCoroutineScope()


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
                    tags.forEach { tagName ->
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
                        coroutinescope.launch {
                            viewModel.refreshTags(
                                onSuccess = { it ->
                                    tags = it
                                    println("refreshed tags: $tags")
                                },
                                context = context!!
                            )

                        }

                    })
                    .align(Alignment.CenterVertically)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))
    }

}


@Composable
fun ProfileScreen(context: ComponentActivity, viewModel: AppMainViewModel, navController: NavHostController, userId: Int? = null ) {
    if (userId == null) MyProfileScreen(context, viewModel, navController)
    else UserProfileScreen(context, viewModel, navController, userId)
}


@Composable
fun UserProfileScreen(context: ComponentActivity, viewModel: AppMainViewModel, navController: NavHostController, userId: Int? = null) {
    var userPosts by remember { mutableStateOf(emptyList<PostDTO>()) }
    var userInfo by remember { mutableStateOf(UserInfoDTO(0, "", "", "", listOf(), false,0, 0)) }
    var loading by remember { mutableStateOf(true) } //이때는 유저 프로필까지 가져와야 한다.

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

            item {ProfileRow(
                viewModel = viewModel,
                userInfo = userInfo,
                onClick = {
                    /* TODO: toggle follow */
                },
                buttonText = if (userInfo.is_followed) "팔로잉" else "팔로우하기",
                itsMe = false,
                context = context,
            )}

            item {Spacer(modifier = Modifier.height(16.dp))}

            items(userPosts) { post ->
                ProfilePost(post = post, viewModel = viewModel, isCurrentUser = false)
            }

            // navigation bottom app bar 때문에 스크롤이 가려지는 것 방지 + 20.dp padding
            item {Spacer(modifier = Modifier.height(70.dp))}
        }
    }
}

@Composable
fun MyProfileScreen(context: ComponentActivity, viewModel: AppMainViewModel, navController: NavHostController, userId: Int? = null) {
    var userPosts by remember { mutableStateOf(emptyList<PostDTO>()) }
    var myInfo = viewModel.myInfo
    var selectedTab by remember { mutableStateOf("MY") }
    var loading by remember { mutableStateOf(true) } //이때는 프로필은 고정이고, 하위 feed만 로딩하면 된다

    LaunchedEffect(selectedTab) {
        loading = true
        try {
            if(selectedTab == "MY") viewModel.getUserFeed(
                userId = userId,
                onSuccess = { info, posts ->
                    userPosts = posts
                }
            )

            else viewModel.getLikedFeed (
                onSuccess = { posts ->
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

    LazyColumn(
        state = rememberLazyListState(),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)) {

        item {
            ProfileRow(
                viewModel = viewModel,
                userInfo = myInfo,
                onClick = {
                   navigateToDestination(navController, "EditProfile")
                },
                buttonText = "프로필 편집",
                itsMe = true,
                context = context,
            )
        }

        item {
            TabRow(
                selectedTabIndex = if (selectedTab == "MY") 0 else 1,
                containerColor = Color.White,
                contentColor = Color.Black,
            ) {
                Tab(
                    text = { Text("MY") },
                    selected = selectedTab == "MY",
                    onClick = { selectedTab = "MY"; loading = true },
                    selectedContentColor = MainColor,
                    unselectedContentColor = Gray,
                )
                Tab(
                    text = { Text("LIKED") },
                    selected = selectedTab == "LIKED",
                    onClick = { selectedTab = "LIKED"; loading = true },
                    selectedContentColor = MainColor,
                    unselectedContentColor = Gray,
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        if(loading) {
            item { Spacer(modifier = Modifier.height(30.dp))}
            item {
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
        }

        else {
            if (selectedTab == "MY") items(userPosts) { post -> //내가 쓴 리뷰들
                ProfilePost(post = post, viewModel = viewModel, isCurrentUser = true)
            }
            else items(userPosts) { post -> //좋아요한 리뷰들 -> 이 경우에만 toggleLike하면 delete되어야 하므로 isLikedPost = true
                HomePost(post = post, viewModel = viewModel, navHostController = navController, isLikedPost = true)
            }
        }

        // navigation bottom app bar 때문에 스크롤이 가려지는 것 방지 + 20.dp padding
        item { Spacer(modifier = Modifier.height(70.dp)) }
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


@Composable
fun ProfilePost(post: PostDTO, viewModel: AppMainViewModel, isCurrentUser: Boolean) {
    val coroutinescope = rememberCoroutineScope()
    var deleted by remember { mutableStateOf(false) }


    AnimatedVisibility(
        visible = !deleted, // Show only when not deleted
        enter = fadeIn(), // Fade in animation
        exit = fadeOut() // Fade out animation when deleted
    ) {
        Column() {
            Post(
                post = post,
                onHeartClick = {
                    coroutinescope.launch {
                        viewModel.toggleLike(post.id)
                    }
                },
                canDelete = isCurrentUser,
                onDelete = {
                    coroutinescope.launch {
                        viewModel.deletePost(post.id)
                        deleted = true
                    }
                }
            )
        }
    }
}
