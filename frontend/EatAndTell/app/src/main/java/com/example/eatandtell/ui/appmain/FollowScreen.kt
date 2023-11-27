package com.example.eatandtell.ui.appmain

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eatandtell.dto.UserDTO
import com.example.eatandtell.ui.Profile
@Composable
fun FollowScreen(
    context: ComponentActivity,
    viewModel: AppMainViewModel,
    navHostController: NavHostController,
    type: FollowScreenType,
    userId: Int? = null
) {
    val followScreenFactory = FollowScreenFactory()
    val FollowScreen = followScreenFactory.createFollowScreen(type, context, viewModel, navHostController, userId)
    FollowScreen()
}

@Composable
fun FollowRow(
    user: UserDTO,
    viewModel: AppMainViewModel,
    navHostController: NavHostController
) {
    Row {
        Profile(
            user.avatar_url,
            user.username,
            user.description,
            onClick = {
                if (user.id == viewModel.myProfile.id)
                    navHostController.navigate( "Profile")
                else
                    navHostController.navigate( "Profile/${user.id}")
            },
        )
    }
}



