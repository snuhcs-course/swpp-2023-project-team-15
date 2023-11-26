package com.example.eatandtell.ui.appmain

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.UserInfoDTO
import com.example.eatandtell.ui.UpButton
import com.example.eatandtell.ui.showToast
import com.example.eatandtell.ui.theme.Gray
import com.example.eatandtell.ui.theme.MainColor
import com.example.eatandtell.ui.theme.PaleGray
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class ProfileScreenFactory: IProfileScreenFactory {

    @Composable
    override fun createProfileScreen(
        userId: Int?,
        context: ComponentActivity,
        viewModel: AppMainViewModel,
        navController: NavHostController
    ): @Composable () -> Unit {
        return if (userId == null || userId == viewModel.myProfile.id) {
            { MyProfileScreen(context, viewModel, navController, userId) }
        } else {
            { UserProfileScreen(context, viewModel, navController, userId) }
        }
    }

    @Composable
    fun MyProfileScreen(
        context: ComponentActivity,
        viewModel: AppMainViewModel,
        navController: NavHostController,
        userId: Int? = null
    ) {
//        val feedPosts = remember { mutableStateListOf<PostDTO>() }
//        var myInfo by remember { mutableStateOf(UserInfoDTO(0, "", "", "", listOf(), false, 0, 0)) }
//        var selectedTab by remember { mutableStateOf("MY") }
//        var loading by remember { mutableStateOf(1) }
        // loading == 1: 전체 로딩
        // loading == 2: 하위 피드만 재로딩
        // loading == 0: 로딩 안함
//        val uploadMessage by viewModel.uploadStatus.observeAsState()
//        LaunchedEffect(key1 = uploadMessage){
//            uploadMessage?.let{
//                showToast(context,it)
//            }
//        }

        var lazyListState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        Log.d("navigateToDestination", "lazylist in MyProfile: ${lazyListState}")

        val feedPosts = viewModel.profilePosts
        val myInfo = viewModel.myInfo.value
        var loading by remember { mutableStateOf(1) } // Consider making this state part of the ViewModel too
        var selectedTab by remember { mutableStateOf("MY") }

        LaunchedEffect(selectedTab, loading) {
            if (loading != 0) {
                viewModel.loadProfileData(userId, loading, selectedTab)
                loading = 0
            }
        }

        LaunchedEffect(viewModel.loadError) {
            viewModel.loadError.value?.let { error ->
                showToast(context, error)
                viewModel.loadError.value = null // Reset the error state after showing the toast
            }
        }

        if (loading == 1) {
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
        } else {
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
                        navController =navController,
                        onClick = {
//                            navigateToDestination(navController, "EditProfile")
                            navController.navigate("EditProfile")
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
                    if (selectedTab == "MY") items(
                        items = feedPosts,
                        key = { it.id }) { post -> //내가 쓴 리뷰들
                        ProfilePost(post = post,
                            viewModel = viewModel,
                            isCurrentUser = true)
                        Divider(
                            color = PaleGray,
                            thickness = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        )
                    }
                    else items(
                        items = feedPosts,
                        key = { it.id }) { post -> //좋아요한 리뷰들 -> 이 경우에만 toggleLike하면 delete되어야 하므로 isLikedPost = true
                        HomePost(
                            post = post,
                            viewModel = viewModel,
                            navHostController = navController,
                        )
                        Divider(
                            color = PaleGray,
                            thickness = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        )
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

    @Composable
    fun UserProfileScreen(
        context: ComponentActivity,
        viewModel: AppMainViewModel,
        navController: NavHostController,
        userId: Int? = null
    ) {
        val userPosts = remember { mutableStateListOf<PostDTO>() }
        var userInfo by remember {
            mutableStateOf(
                UserInfoDTO(
                    0,
                    "",
                    "",
                    "",
                    listOf(),
                    false,
                    0,
                    0
                )
            )
        }
        var loading by remember { mutableStateOf(true) } //이때는 유저 프로필까지 가져와야 한다.
        var lazyListState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()


        LaunchedEffect(loading) {
            try {
                viewModel.getUserFeed(
                    userId = userId,
                    onSuccess = { info, posts ->
                        userInfo= info
                        userPosts.clear()
                        userPosts.addAll(posts)
                    }
                )
                loading = false
            } catch (e: Exception) {
                if (e !is CancellationException) { // 유저가 너무 빨리 화면을 옮겨다니는 경우에는 CancellationException이 발생할 수 있지만, 서버 에러가 아니라서 패스
                    loading = false
                    println("feed load error $e")
                    showToast(context, "유저 피드 로딩에 실패하였습니다 $e")
                }
            }
        }


        if (loading) {
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
        } else {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {

                item {
                    ProfileRow(
                        viewModel = viewModel,
                        userInfo = userInfo,
                        buttonText = if (userInfo.is_followed) "팔로잉" else "팔로우하기",
                        itsMe = userId == viewModel.myProfile.id, // Example of determining if it's the current user's profile
                        context = context,
                        navController = navController,
                        onClick = {
                            val followerCount =
                                if (userInfo.is_followed) userInfo.follower_count - 1 else userInfo.follower_count + 1
                            userInfo = userInfo.copy(
                                is_followed = !userInfo.is_followed,
                                follower_count = followerCount
                            )
                            coroutineScope.launch {
                                viewModel.toggleFollow(userInfo.id)
                            }
                        },
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
                // Replace the existing items call for userPosts
                items(items = userPosts, key = { it.id }) { post ->
                    ProfilePost(
                        post = post,
                        viewModel = viewModel,
                        isCurrentUser = userId == null, // Or any other logic you have for determining if the user is the current user
                    )
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

}
