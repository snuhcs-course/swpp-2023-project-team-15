package com.example.eatandtell.ui.appmain

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.example.eatandtell.ui.Post
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.UpButton
import com.example.eatandtell.ui.showToast
import com.example.eatandtell.ui.theme.Gray
import com.example.eatandtell.ui.theme.MainColor
import com.example.eatandtell.ui.theme.PaleGray
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(context: ComponentActivity, viewModel: AppMainViewModel,navHostController: NavHostController) {
//    var feedPosts = remember { mutableStateListOf<PostDTO>() }
    val feedPosts = viewModel.posts // Directly use the mutable state list
    var loading by remember { viewModel.isLoading }
    var lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf("추천") }
    val loadError by remember { viewModel.loadError }

    Log.d("navigateToDestination", "lazylist in Home: ${lazyListState}")


    LaunchedEffect(selectedTab) {
        viewModel.loadPosts(selectedTab)
    }
    LaunchedEffect(loadError) {
        loadError?.let { error ->
            showToast(context, error)
            viewModel.loadError.value = null // Reset the error state after showing the toast
        }
    }

    
    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }
//            items(feedPosts) { post ->
//                HomePost(post, viewModel = viewModel, navHostController = navHostController)
//            }

        item {
            TabRow(
                selectedTabIndex = if (selectedTab == "추천") 0 else 1,
                containerColor = Color.White,
                contentColor = Color.Black,
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[if (selectedTab == "추천") 0 else 1])
                            .height(3.dp) // 인디케이터의 높이
                            .background(MainColor) // 여기에서 MainColor로 색상 지정
                    )
                }
            ) {
                Tab(
                    text = { Text("추천") },
                    selected = selectedTab == "추천",
                    onClick = { selectedTab = "추천"; loading = true },
                    selectedContentColor = MainColor,
                    unselectedContentColor = Gray,
                )
                Tab(
                    text = { Text("팔로잉") },
                    selected = selectedTab == "팔로잉",
                    onClick = { selectedTab = "팔로잉"; loading = true },
                    selectedContentColor = MainColor,
                    unselectedContentColor = Gray,
                )
            }
        }

        item {Spacer(modifier = Modifier.height(14.dp))}

        if(loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 200.dp),
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
        }
        else {
            items(items = feedPosts, key = { it.id }) { post ->
//                HomePost(
//                    post = post,
//                    viewModel = viewModel,
//                    navHostController = navHostController,
//                    onDelete = { postToDelete ->
//                        feedPosts.remove(postToDelete)
//                    }
//                ) { postToLike ->
//                    val index = feedPosts.indexOf(postToLike)
//                    if (index != -1) {
//                        // Determine the new like count based on the current is_liked state
//                        val newLikeCount =
//                            if (postToLike.is_liked) postToLike.like_count - 1 else postToLike.like_count + 1
//                        // Update the post with the new like state and count
//                        feedPosts[index] = postToLike.copy(
//                            is_liked = !postToLike.is_liked,
//                            like_count = newLikeCount
//                        )
//                    }
//                }
                HomePost(
                    post = post,
                    viewModel = viewModel,
                    navHostController = navHostController
                )
                Divider(
                    color = PaleGray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                )            }
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

@Composable
fun HomePost(
    post: PostDTO,
    viewModel: AppMainViewModel,
    navHostController: NavHostController,
//    onDelete: (PostDTO) -> Unit,
//    onLike: (PostDTO) -> Unit
) {
    val user = post.user
    val coroutinescope = rememberCoroutineScope()
    var deleted by remember { mutableStateOf(false) }


    AnimatedVisibility(
        visible = !deleted, // Show only when not deleted
        enter = fadeIn(), // Fade in animation
        exit = fadeOut() // Fade out animation when deleted
    ) {
        Column() {
            Profile(
                user.avatar_url,
                user.username,
                user.description,
                onClick = {
                    if (user.id == viewModel.myProfile.id)
                        navigateToDestination(navHostController, "Profile")
                    else
                        navigateToDestination(navHostController, "Profile/${user.id}")
                },
            )
            Spacer(modifier = Modifier.height(11.dp))
            Post(
                post = post,
                onHeartClick = {
//                    onLike(post)
                    coroutinescope.launch {
                        viewModel.toggleLike(post.id)

                    }
                },
                canDelete = (user.id == viewModel.myProfile.id),
                onDelete = {
                    deleted = true
//                    onDelete(post)
                    coroutinescope.launch {
                        viewModel.deletePost(post.id)
                    }
                }
            )
        }
    }
}


