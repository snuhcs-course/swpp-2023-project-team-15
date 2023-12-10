package com.swpp2023.eatandtell.ui.appmain

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.swpp2023.eatandtell.dto.PostDTO
import com.swpp2023.eatandtell.dto.UserInfoDTO
import com.swpp2023.eatandtell.ui.CustomButton
import com.swpp2023.eatandtell.ui.FollowText
import com.swpp2023.eatandtell.ui.Post
import com.swpp2023.eatandtell.ui.ProfileImage
import com.swpp2023.eatandtell.ui.ProfileText
import com.swpp2023.eatandtell.ui.Tag
import com.swpp2023.eatandtell.ui.showToast
import com.swpp2023.eatandtell.ui.theme.Inter
import com.swpp2023.eatandtell.ui.theme.MainColor
import com.swpp2023.eatandtell.ui.theme.PaleGray
import com.swpp2023.eatandtell.ui.theme.White
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch


@Composable
fun ProfileRow(
    viewModel: AppMainViewModel, userInfo: UserInfoDTO,
    navController: NavHostController, onClick: () -> Unit, buttonText: String, itsMe: Boolean = false, context: ComponentActivity? = null, // Added this parameter
) {
    var tags by rememberSaveable { mutableStateOf(userInfo.tags) }
    val coroutinescope = rememberCoroutineScope()
//    var isFollowing by remember { mutableStateOf(userInfo.is_followed) }
    var expanded by remember { mutableStateOf(false) }
    val loadError by viewModel.loadError.collectAsState() // Observing StateFlow for error

    LaunchedEffect(loadError) {
        loadError?.let { error ->
            if (context != null) {
                showToast(context, error)
            }
            viewModel.resetLoadError() // Reset error via ViewModel method
        }
    }
    Column {
        Spacer(modifier = Modifier.height(10.dp))
        // First row with profile image, follower, and following
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 11.dp, horizontal = 8.dp), // Add padding as needed
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileImage(profileUrl = userInfo.avatar_url, size = 60.dp)
            Spacer(modifier = Modifier.width(95.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FollowText(count = userInfo.following_count, label = "팔로잉",
                    onClick={
                        navController.navigate("Following/${userInfo.id}")
                    })
            }
            Spacer(modifier = Modifier.width(50.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FollowText(count = userInfo.follower_count, label = "팔로워",
                    onClick={
                        navController.navigate("Follower/${userInfo.id}" )
                    })
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
            ProfileText(username = userInfo.username, userInfo.description)

            if (itsMe) CustomButton(onClick = onClick, text = buttonText, containerColor = PaleGray)
            else if (buttonText == "팔로우하기") {
                CustomButton(onClick = {
                    onClick()
                }, text = buttonText, containerColor = PaleGray, borderColor = PaleGray, testTag = "follow_button")

            } else {
                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                    CustomButton(
                        onClick = {
                            expanded = true
                        },
                        text = buttonText,
                        containerColor = White,
                        borderColor = PaleGray,
                        testTag = "unfollow_button"
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset(x = 0.dp, y = with(LocalDensity.current) { 6.dp })
                    ) {
                        DropdownMenuItem(
                            text = { Text("팔로우 취소", modifier = Modifier.fillMaxWidth(),textAlign = TextAlign.Center)},
                            onClick = { onClick(); expanded = false },
                            modifier = Modifier.height(28.dp).width(100.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(11.dp))

        //Tags
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {

            if (tags.isEmpty()) {
                //show Text to refresh tags
                Text(
                    text = "아직 태그가 없습니다",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight(500),
                        color = Color.Gray,
                    ),
                    modifier = Modifier
                        .weight(1f),
                )
            } else {
                FlowRow(
                    modifier = Modifier
                        .weight(1f),
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    tags.forEach { tagName ->
                        Tag(tagName) {}// Passing an empty lambda to indicate no action on click
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
                ) {
                    CustomButton(
                        onClick = {
                            coroutinescope.launch {
                                try{
                                viewModel.refreshTags(
                                    onSuccess = { newTags ->

                                        // Check and cast newTags to List<String>
                                        if (newTags is List<*>) {
                                            @Suppress("UNCHECKED_CAST")
                                            println("refreshed tags: $tags")
                                            if (context != null) {
                                                if(newTags.isEmpty()){
                                                    showToast(context, "아직 태그가 없습니다")
                                                }
                                                else if (newTags.sorted() == tags.sorted()) {
                                                    showToast(context, "태그가 변경되지 않았습니다.")
                                                }
                                                else {
                                                    showToast(context, "태그가 업데이트되었습니다")
                                                }
                                            }
                                            tags = newTags as List<String>

                                        } else {
                                            println("Error: Expected a list of tags, but received something else.")
                                        }
                                    },
                                    context = context!!
                                )
                                } catch (e: Exception) {
                                }
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
    fun ProfileScreen(
        context: ComponentActivity,
        viewModel: AppMainViewModel,
        navController: NavHostController,
        userId: Int? = null
    ) {
        val profileScreenFactory = ProfileScreenFactory()

        // Create the profile screen using the factory
        val ProfileScreen = profileScreenFactory.createProfileScreen(userId, context, viewModel, navController)

        // Now, you can call ProfileScreen() to display the correct profile screen
        ProfileScreen()
    }




    @Composable
    fun ProfilePost(
        post: PostDTO,
        viewModel: AppMainViewModel,
        isCurrentUser: Boolean,
        context: ComponentActivity,
    ) {
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
                            val res = viewModel.deletePost(post.id)
                            if(res) {
                                deleted = true
                            }
                        }
                    }
                )
            }
        }
    }


