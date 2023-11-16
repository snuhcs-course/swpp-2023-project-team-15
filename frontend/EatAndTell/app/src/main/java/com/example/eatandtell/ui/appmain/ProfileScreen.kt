package com.example.eatandtell.ui.appmain
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.UserInfoDTO
import com.example.eatandtell.ui.CustomButton
import com.example.eatandtell.ui.FollowText
import com.example.eatandtell.ui.Post
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.ProfileImage
import com.example.eatandtell.ui.ProfileText
import com.example.eatandtell.ui.Tag
import com.example.eatandtell.ui.UpButton
import com.example.eatandtell.ui.showToast
import com.example.eatandtell.ui.theme.Black
import com.example.eatandtell.ui.theme.Gray
import com.example.eatandtell.ui.theme.Inter
import com.example.eatandtell.ui.theme.MainColor
import com.example.eatandtell.ui.theme.PaleGray
import com.example.eatandtell.ui.theme.PaleOrange
import com.example.eatandtell.ui.theme.White

import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch


@Composable
fun ProfileRow(viewModel: AppMainViewModel, userInfo: UserInfoDTO, onClick: () -> Unit, buttonText: String, itsMe : Boolean = false, context : ComponentActivity? = null, // Added this parameter
) {
    var tags by rememberSaveable { mutableStateOf(userInfo.tags) }
    val coroutinescope = rememberCoroutineScope()

    var isFollowing by remember { mutableStateOf(userInfo.is_followed) }

    Column {

            Spacer(modifier = Modifier.height(15.dp))
            // First row with profile image, follower, and following
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 11.dp, horizontal = 8.dp), // Add padding as needed
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileImage(profileUrl = userInfo.avatar_url, size = 60.dp)
                Spacer(modifier =   Modifier.width(95.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FollowText(count = userInfo.following_count, label ="팔로잉" )
                }
                Spacer(modifier =   Modifier.width(50.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FollowText(count = userInfo.follower_count, label ="팔로워" )
                }
            }

            // Second row with profile text and edit profile button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp, vertical = 8.dp), // Adjust padding as needed
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileText(username = userInfo.username,  userInfo.description)

                if (itsMe) CustomButton(onClick = onClick, text = buttonText, containerColor = PaleGray)
                else if (buttonText == "팔로우하기") {
                    CustomButton(onClick = {
                        onClick()
                    }, text = buttonText, containerColor = PaleGray, borderColor = PaleGray)
                } else {
                    CustomButton(onClick = {
                        onClick()
                    }, text = buttonText, containerColor = White, borderColor = PaleGray)
                }
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
                        Tag(tagName) {} // Passing an empty lambda to indicate no action on click
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))

        //refresh button
        if (itsMe) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ){
                CustomButton(
                    onClick = {
                        coroutinescope.launch {
                            viewModel.refreshTags(
                                onSuccess = { newTags ->
                                    // Check and cast newTags to List<String>
                                    if (newTags is List<*>) {
                                        @Suppress("UNCHECKED_CAST")
                                        tags = newTags as List<String>
                                        println("refreshed tags: $tags")
                                    } else {
                                        println("Error: Expected a list of tags, but received something else.")
                                    }
                                },
                                context = context!!
                            )
                        }
                    },
                    text = "태그 갱신",
                    textColor = White,
                    fontWeight = 900,
                    containerColor = MainColor,
                    borderColor = PaleGray,
                    cornerRadius = 15.dp,
                    height = 45.dp,
                    widthFraction = 1f,
                    icon = Icons.Default.Refresh // Pass the refresh icon
                )
            }
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
//    var userPosts by remember { mutableStateOf(emptyList<PostDTO>()) }
    val userPosts = remember { mutableStateListOf<PostDTO>() }
    var userInfo by remember { mutableStateOf(UserInfoDTO(0, "", "", "", listOf(), false,0, 0)) }
    var loading by remember { mutableStateOf(true) } //이때는 유저 프로필까지 가져와야 한다.
    var lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(loading) {
        try {
            viewModel.getUserFeed(
                userId = userId,
                onSuccess = { info, posts ->
                    userInfo = info
                    userPosts.clear()
                    userPosts.addAll(posts)
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
                    .size(70.dp),
                color = MainColor
            )
        }
    }

    else {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)) {


            item {
                ProfileRow(
                    viewModel = viewModel,
                    userInfo = userInfo,
                    buttonText = if (userInfo.is_followed) "팔로잉" else "팔로우하기",
                    itsMe = userId == viewModel.myProfile.id, // Example of determining if it's the current user's profile
                    context = context,
                    onClick = {
                        /* TODO: toggle follow */
                        val followerCount = if (userInfo.is_followed) userInfo.follower_count - 1 else userInfo.follower_count + 1
                        userInfo = userInfo.copy(is_followed = !userInfo.is_followed, follower_count = followerCount)
                        coroutineScope.launch {
                            viewModel.toggleFollow(userInfo.id)
                        }
                    },
                )
            }


            item {Spacer(modifier = Modifier.height(16.dp))}
            // Replace the existing items call for userPosts
            items(items = userPosts, key = { it.id }) { post ->
                ProfilePost(
                    post = post,
                    viewModel = viewModel,
                    isCurrentUser = userId == null, // Or any other logic you have for determining if the user is the current user
                    onLike = { postToLike ->
                        val index = userPosts.indexOf(postToLike)
                        if (index != -1) {
                            val newLikeCount = if (postToLike.is_liked) postToLike.like_count - 1 else postToLike.like_count + 1
                            userPosts[index] = postToLike.copy(
                                is_liked = !postToLike.is_liked,
                                like_count = newLikeCount
                            )
                        }
                    },
                    onDelete = {postToDelete ->
                        }
                )
            }


            // navigation bottom app bar 때문에 스크롤이 가려지는 것 방지 + 20.dp padding
            item {Spacer(modifier = Modifier.height(70.dp))}
        }

        UpButton {
            coroutineScope.launch {
                lazyListState.animateScrollToItem(0)
            }
        }
    }
}

@Composable
fun MyProfileScreen(context: ComponentActivity, viewModel: AppMainViewModel, navController: NavHostController, userId: Int? = null) {
    val feedPosts = remember { mutableStateListOf<PostDTO>() }
    var myInfo by remember { mutableStateOf(UserInfoDTO(0, "", "", "", listOf(), false,0, 0)) }
    var selectedTab by remember { mutableStateOf("MY") }
    var loading by remember { mutableStateOf(1) }
    // loading == 1: 전체 로딩
    // loading == 2: 하위 피드만 재로딩
    // loading == 0: 로딩 안함


    var lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Log.d("navigateToDestination", "lazylist in MyProfile: ${lazyListState}")


    LaunchedEffect(selectedTab, loading) {
        try {
            if(loading == 1) viewModel.getUserFeed( //전체 로딩
                userId = userId,
                onSuccess = { info, posts ->
                    myInfo = info
                    feedPosts.clear()
                    feedPosts.addAll(posts)
                }
            )

            else if (loading == 2 && selectedTab == "MY") viewModel.getUserFeed( //하위 피드만 재로딩
                userId = userId,
                onSuccess = { info, posts ->
                    feedPosts.clear()
                    feedPosts.addAll(posts)
                }
            )

            else if (loading == 2) viewModel.getLikedFeed ( //하위 피드만 재로딩
                onSuccess = { posts ->
                    feedPosts.clear()
                    feedPosts.addAll(posts)
                }
            )
            loading = 0
        }
        catch (e: Exception) {
            if (e !is CancellationException) { // 유저가 너무 빨리 화면을 옮겨다니는 경우에는 CancellationException이 발생할 수 있지만, 서버 에러가 아니라서 패스
                loading = 0
                println("feed load error $e")
                showToast(context, "유저 피드 로딩에 실패하였습니다 $e")
            }
        }
    }

    if(loading == 1) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                //로딩 화면
                modifier = Modifier
                    .size(70.dp),
                color = MainColor
            )
        }
    }



    else {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {

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
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[if (selectedTab == "MY") 0 else 1])
                                .height(3.dp) // 인디케이터의 높이
                                .background(MainColor) // 여기에서 MainColor로 색상 지정
                        )
                    }
                ) {
                    Tab(
                        text = { Text("MY") },
                        selected = selectedTab == "MY",
                        onClick = { selectedTab = "MY"; loading = 2 },
                        selectedContentColor = MainColor,
                        unselectedContentColor = Gray,
                    )
                    Tab(
                        text = { Text("LIKED") },
                        selected = selectedTab == "LIKED",
                        onClick = { selectedTab = "LIKED"; loading = 2 },
                        selectedContentColor = MainColor,
                        unselectedContentColor = Gray,
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            if (loading == 2) {
                item { Spacer(modifier = Modifier.height(30.dp)) }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            //로딩 화면
                            modifier = Modifier
                                .size(70.dp),
                            color = MainColor
                        )
                    }
                }
            } else {
                if (selectedTab == "MY") items(items = feedPosts, key = { it.id }) { post -> //내가 쓴 리뷰들
                    ProfilePost(post = post, viewModel = viewModel, isCurrentUser = true, onLike = { postToLike ->
                        val index = feedPosts.indexOf(postToLike)
                        if (index != -1) {
                            val newLikeCount = if (postToLike.is_liked) postToLike.like_count - 1 else postToLike.like_count + 1
                            feedPosts[index] = postToLike.copy(
                                is_liked = !postToLike.is_liked,
                                like_count = newLikeCount
                            )
                        }
                    },
                        onDelete = { postToDelete ->
                            feedPosts.remove(postToDelete)
                        })
                }
                else items(items = feedPosts, key = { it.id }) { post -> //좋아요한 리뷰들 -> 이 경우에만 toggleLike하면 delete되어야 하므로 isLikedPost = true
                    HomePost(
                        post = post,
                        viewModel = viewModel,
                        navHostController = navController,
                        onDelete = { postToDelete ->
                            feedPosts.remove(postToDelete)
                        }

                    ) { postToLike ->
                        val index = feedPosts.indexOf(postToLike)
                        if (index != -1) {
                            // Determine the new like count based on the current is_liked state
                            val newLikeCount =
                                if (postToLike.is_liked) postToLike.like_count - 1 else postToLike.like_count + 1
                            // Update the post with the new like state and count
                            feedPosts[index] = postToLike.copy(
                                is_liked = !postToLike.is_liked,
                                like_count = newLikeCount
                            )
                        }
                    }
                }
            }

            // navigation bottom app bar 때문에 스크롤이 가려지는 것 방지 + 20.dp padding
            item { Spacer(modifier = Modifier.height(70.dp)) }
        }

        UpButton {
            coroutineScope.launch {
                lazyListState.animateScrollToItem(0)
            }
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
            buttonText = "팔로우하기" , // New buttonText parameter
        )
    }
}


@Composable
fun ProfilePost(post: PostDTO, viewModel: AppMainViewModel, isCurrentUser: Boolean, onLike: (PostDTO) -> Unit,
                onDelete: (PostDTO) -> Unit) {
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
                    onLike(post)
                    coroutinescope.launch {
                        viewModel.toggleLike(post.id)
                    }
                },
                canDelete = isCurrentUser,
                onDelete = {
                    deleted = true
                    onDelete(post)
                    coroutinescope.launch {
                        viewModel.deletePost(post.id)
                    }
                }
            )
        }
    }
}
