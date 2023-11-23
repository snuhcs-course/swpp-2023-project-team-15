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
                    navigateToDestination(navHostController, "Profile")
                else
                    navigateToDestination(navHostController, "Profile/${user.id}")
            },
        )
    }
}

@Composable
fun FollowerScreen(context: ComponentActivity, viewModel: AppMainViewModel,navHostController: NavHostController,
                   user_id:Int? =null){
    var followerUsers by remember { mutableStateOf(emptyList<UserDTO>()) }
    //var loading by remember { mutableStateOf(true) }
    var lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit){
    viewModel.getFollowers(user_id= user_id, onSuccess ={users->
        followerUsers= users
    } )
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(followerUsers.size){index->
            val user= followerUsers[index]
            FollowRow(user= user, viewModel = viewModel, navHostController = navHostController)

        }
    }
}


@Composable
fun FollowingScreen(context: ComponentActivity, viewModel: AppMainViewModel,navHostController: NavHostController,
                    user_id:Int?=null){
    var followingUsers by remember { mutableStateOf(emptyList<UserDTO>()) }
    var lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit){
        viewModel.getFollowings(user_id= user_id, onSuccess ={users->
            followingUsers= users
        } )
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(items=followingUsers){user->
            FollowRow(user= user, viewModel = viewModel, navHostController = navHostController)

        }
    }
}


