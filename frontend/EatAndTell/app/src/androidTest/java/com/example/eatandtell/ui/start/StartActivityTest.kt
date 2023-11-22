package com.example.eatandtell.ui.start

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.eatandtell.data.api.ApiService
import com.example.eatandtell.ui.appmain.AppMainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class StartActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<StartActivity>()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    lateinit var navController: TestNavHostController
    lateinit var mockViewModel: StartViewModel

    @Test
    fun test_StartActivity_InitialDestination_IsLogin() {
        composeTestRule.onNodeWithTag("go_to_signup").assertIsDisplayed()
    }
    @Test
    fun testLoginScreen_withValidCredentials_navigatesToHome() {
        val validId = "integration_test"
        val validPassword = "qwer1234"
        composeTestRule.onNodeWithText("아이디를 입력하세요").performTextInput(validId)

        // Input the valid password
        composeTestRule.onNodeWithText("비밀번호를 입력하세요").performTextInput(validPassword)

        // Click on the Login button
        composeTestRule.onNodeWithText("로그인").assertIsDisplayed().performClick()

        // TODO: assert the screen transition is actually done
    }

    @Test
    fun test_StartActivity_NavigateToSignup() {
        composeTestRule.onNodeWithTag("go_to_signup").performClick()
        composeTestRule.onNodeWithTag("go_to_login").assertIsDisplayed()
    }
}