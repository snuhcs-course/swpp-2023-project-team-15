package com.example.eatandtell.ui.appmain

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

interface IFollowScreenFactory {
    @Composable
    fun createFollowScreen(
        type: FollowScreenType,
        context: ComponentActivity,
        viewModel: AppMainViewModel,
        navHostController: NavHostController,
        userId: Int?
    ): @Composable () -> Unit
}
