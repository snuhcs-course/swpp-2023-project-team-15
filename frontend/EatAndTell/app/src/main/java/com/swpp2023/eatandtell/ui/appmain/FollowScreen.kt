package com.swpp2023.eatandtell.ui.appmain

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.swpp2023.eatandtell.dto.UserDTO
import com.swpp2023.eatandtell.ui.Profile
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



