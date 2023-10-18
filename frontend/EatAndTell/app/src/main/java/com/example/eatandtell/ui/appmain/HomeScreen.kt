package com.example.eatandtell.ui.appmain

import androidx.activity.ComponentActivity
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.ui.HeartEmpty
import com.example.eatandtell.ui.HeartFull
import com.example.eatandtell.ui.PostImage
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.StarRating
import com.example.eatandtell.ui.showToast
import com.example.eatandtell.ui.theme.Black
import com.example.eatandtell.ui.theme.MainColor
import kotlinx.coroutines.launch


//TODO: parameter PostDTO로 바꾸기

@Composable
fun Post(
//    restaurantName: String,
//    rating: String,
//    imageUrls: List<String>,
//    restaurantDescription: String,
    post : PostDTO,
    isLiked : Boolean,
    likes : Int,
) {

    val restaurantName = post.restaurant.name
    val rating = post.rating
    //get list of photo urls from post.photos list's photo_url
    val imageUrls = if (post.photos!=null) post.photos.map { photo -> photo.photo_url } else listOf()
    val description = post.description

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            //식당 이름
            Text(text = restaurantName, style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight(700),
                color = Black,
            ), modifier = Modifier
                .weight(1f)
                .height(20.dp),
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(4.dp))

            //ratings
            Row(
                horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .width(80.dp)
                    .height(16.dp)
            ) {
                StarRating(rating)
                // 다른 child views 추가
            }
        }

        Spacer(modifier = Modifier.height(7.dp))

        // Images Row
        if (imageUrls.isNotEmpty()) {
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
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Restaurant Description
        Text(text = description, style = TextStyle(
            fontSize = 14.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF262626),
        ), modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
            overflow = TextOverflow.Ellipsis)

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = likes.toString(),
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 16.5.sp,
                    fontWeight = FontWeight(500),
                    color = MainColor,
                ),
                modifier = Modifier
                    .width(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            if(isLiked) HeartFull() else HeartEmpty()
        }
    }
}

@Composable
fun HomePost(profileUrl: String,
             username: String,
             userDescription: String,
             post: PostDTO,
             isLiked : Boolean,
             likes : Int,){
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Profile Row
        Spacer(modifier = Modifier.height(8.dp))
        Profile(profileUrl, username, userDescription);
        Spacer(modifier = Modifier.height(11.dp))
        Post(
            post,
            isLiked,
            likes
        )
    }
}


@Composable
fun HomeScreen(context: ComponentActivity, viewModel: AppMainViewModel) {
    var feedPosts by remember { mutableStateOf(emptyList<PostDTO>()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(loading) {
        try {
            viewModel.getAllPosts(
                context,
                onSuccess = { posts ->
                    feedPosts = posts
                    println("feedPosts: ${feedPosts.size}")
                },
            )
            loading = false
        }
        catch (e: Exception) {
            println("getAllPosts error: ${e.message}")
            showToast(context, "getAllPosts error: ${e.message}")
        }

    }

    if(loading) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                //put at the center of the screen
                modifier = Modifier
                    .size(100.dp)
            )
        }
    }
    else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            for (post in feedPosts) {
                Spacer(modifier = Modifier.height(8.dp))
                Profile(
                    profileUrl = "https://newprofilepic.photo-cdn.net//assets/images/article/profile.jpg?90af0c8",
                    username = "Joshua-i",
                    userDescription = "고독한 미식가"
                );
                Spacer(modifier = Modifier.height(11.dp))
                Post(
                    post = post,
                    isLiked = false,
                    likes = 36,
                )
            }

            // navigation bottom app bar 때문에 스크롤이 가려지는 것 방지 + 20.dp padding
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

