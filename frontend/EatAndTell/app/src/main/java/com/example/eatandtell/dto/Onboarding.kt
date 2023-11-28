package com.example.eatandtell.dto

import androidx.annotation.DrawableRes

enum class OnboardingPageType {
    typeA, typeB
}
data class OnboardingPage(
    val type: OnboardingPageType
    val description: String,
    @DrawableRes val title: Int,
    @DrawableRes val image: Int
)