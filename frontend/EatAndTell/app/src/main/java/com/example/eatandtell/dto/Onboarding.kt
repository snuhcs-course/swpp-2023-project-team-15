package com.example.eatandtell.dto

import androidx.annotation.DrawableRes

data class OnboardingPage(
    val description: String,
    @DrawableRes val title: Int,
    @DrawableRes val image: Int
)