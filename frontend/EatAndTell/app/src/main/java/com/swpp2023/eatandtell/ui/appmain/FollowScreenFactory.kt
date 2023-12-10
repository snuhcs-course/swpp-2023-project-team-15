package com.swpp2023.eatandtell.ui.appmain

import androidx.activity.ComponentActivity
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

class FollowScreenFactory : IFollowScreenFactory {
    @Composable
    override fun createFollowScreen(
        type: FollowScreenType,
        context: ComponentActivity,
        viewModel: AppMainViewModel,
        navHostController: NavHostController,
        userId: Int?
    ): @Composable () -> Unit {
        return when (type) {
            FollowScreenType.FOLLOWER -> { { FollowerScreen(context, viewModel, navHostController, userId) } }
            FollowScreenType.FOLLOWING -> { { FollowingScreen(context, viewModel, navHostController, userId) } }
        }
    }

    @Composable
    fun FollowerScreen(context: ComponentActivity, viewModel: AppMainViewModel,navHostController: NavHostController,
                       user_id:Int? =null){
        val followerUsers by viewModel.followers.collectAsState()
//    var followerUsers by remember { mutableStateOf(emptyList<UserDTO>()) }
        var lazyListState = rememberLazyListState()

//    LaunchedEffect(Unit){
//    viewModel.getFollowers(user_id= user_id, onSuccess ={users->
//        followerUsers= users
//    } )
//    }
        LaunchedEffect(Unit) {
            viewModel.getFollowers(user_id)
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
            items(followerUsers) { user ->
                FollowRow(user = user, viewModel = viewModel, navHostController = navHostController)
            }

//        items(followerUsers.size){index->
//            val user= followerUsers[index]
//            FollowRow(user= user, viewModel = viewModel, navHostController = navHostController)
//
//        }
        }
    }


    @Composable
    fun FollowingScreen(context: ComponentActivity, viewModel: AppMainViewModel,navHostController: NavHostController,
                        user_id:Int?=null){
//    var followingUsers by remember { mutableStateOf(emptyList<UserDTO>()) }
        val followingUsers by viewModel.followings.collectAsState()
        var lazyListState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

//    LaunchedEffect(Unit){
//        viewModel.getFollowings(user_id= user_id, onSuccess ={users->
//            followingUsers= users
//        } )
//    }
        LaunchedEffect(Unit) {
            viewModel.getFollowings(user_id)
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
}
