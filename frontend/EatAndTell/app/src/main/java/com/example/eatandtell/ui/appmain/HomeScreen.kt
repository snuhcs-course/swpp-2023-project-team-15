package com.example.eatandtell.ui.appmain

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.UserDTO
import com.example.eatandtell.ui.Post
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.showToast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(context: ComponentActivity, viewModel: AppMainViewModel,navHostController: NavHostController) {
    var feedPosts by remember { mutableStateOf(emptyList<PostDTO>()) }
    var loading by remember { mutableStateOf(true) }
    var myProfile by remember { mutableStateOf(UserDTO(0, "", "", "", listOf())) }

    LaunchedEffect(loading) {
        try {
            println("trying to load home feed")
            viewModel.getAllPosts(
                onSuccess = { posts ->
                    feedPosts = posts
                    println("feedPosts: ${feedPosts.size}")
                },
            )
            println("getting posts is fine")
            viewModel.getMyProfile (
                onSuccess = { it ->
                    myProfile = it
                    println("myProfile: ${myProfile.username}")
                }
            )
            loading = false
        }
        catch (e: Exception) {
            if (e !is CancellationException) { // 유저가 너무 빨리 화면을 옮겨다니는 경우에는 CancellationException이 발생할 수 있지만, 서버 에러가 아니라서 패스
                loading = false
                Log.d("home feed load error", e.toString())
                showToast(context, "홈 피드 로딩에 실패하였습니다")
            }
        }
    }

    if(loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("loading"),
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
                .padding(horizontal = 20.dp)
                .testTag("feed"),
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            items(feedPosts) { post ->
                HomePost(post, viewModel = viewModel, navHostController = navHostController,myProfile)
            }

            // navigation bottom app bar 때문에 스크롤이 가려지는 것 방지 + 20.dp padding
            item { Spacer(modifier = Modifier.height(70.dp)) }
        }

    }
}

@Composable
fun HomePost(post: PostDTO, viewModel: AppMainViewModel,navHostController: NavHostController,myProfile: UserDTO) {
    val user = post.user
    val coroutinescope = rememberCoroutineScope()


    Profile(
        user.avatar_url,
        user.username,
        user.description,
        onClick = {
            if(user.id == myProfile.id)
                navigateToDestination(navHostController, "Profile")
            else
                navigateToDestination(navHostController, "Profile/${user.id}")
        },
    );
    Spacer(modifier = Modifier.height(11.dp))
    Post(
        post = post,
        onHeartClick = {
            coroutinescope.launch {
                viewModel.toggleLike(post.id)
            }
        },
    )
}


