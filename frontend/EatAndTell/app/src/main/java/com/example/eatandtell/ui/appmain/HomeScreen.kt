package com.example.eatandtell.ui.appmain

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.ui.HeartEmpty
import com.example.eatandtell.ui.HeartFull
import com.example.eatandtell.ui.ImageDialog
import com.example.eatandtell.ui.Post
import com.example.eatandtell.ui.PostImage
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.StarRating
import com.example.eatandtell.ui.showToast
import com.example.eatandtell.ui.theme.Black
import com.example.eatandtell.ui.theme.MainColor
import kotlinx.coroutines.launch

//TODO: 이후 좋아요가 PostDTO에 추가되면 like를 PostDTO에서 가져오도록 수정


@Composable
fun HomeScreen(context: ComponentActivity, viewModel: AppMainViewModel) {
    var feedPosts by remember { mutableStateOf(emptyList<PostDTO>()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(loading) {
        try {
            viewModel.getAllPosts(
                onSuccess = { posts ->
                    feedPosts = posts
                    println("feedPosts: ${feedPosts.size}")
                },
            )
            loading = false
        }
        catch (e: Exception) {
            println("home feed load error")
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
                .padding(horizontal = 20.dp),
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            items(feedPosts) { post ->
                HomePost(post, viewModel = viewModel)
            }

            // navigation bottom app bar 때문에 스크롤이 가려지는 것 방지 + 20.dp padding
            item { Spacer(modifier = Modifier.height(70.dp)) }
        }

    }
}

@Composable
fun HomePost(post: PostDTO, viewModel: AppMainViewModel) {
    val user = post.user
    val coroutinescope = rememberCoroutineScope()

    Profile(
        user.avatar_url,
        user.username,
        user.description,
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
