package com.example.eatandtell.ui.start

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<StartActivity>()
    lateinit var navController: TestNavHostController
    lateinit var mockViewModel: StartViewModel

    @Test
    fun test_StartActivity_InitialDestination_IsLogin() {
        composeTestRule.onNodeWithTag("go_to_signup").assertIsDisplayed()
    }

    @Test
    fun test_StartActivity_NavigateToSignup() {
        composeTestRule.onNodeWithTag("go_to_signup").performClick()
        composeTestRule.onNodeWithTag("go_to_login").assertIsDisplayed()
    }
}