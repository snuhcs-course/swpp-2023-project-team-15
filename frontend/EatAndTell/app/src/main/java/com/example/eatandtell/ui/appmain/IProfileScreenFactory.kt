package com.example.eatandtell.ui.appmain

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

interface IProfileScreenFactory {
    @Composable
    fun createProfileScreen(
        userId: Int?,
        context: ComponentActivity,
        viewModel: AppMainViewModel,
        navController: NavHostController
    ): @Composable () -> Unit
}

