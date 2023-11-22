package com.example.eatandtell.ui.appmain

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AppMainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<AppMainActivity>()

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION"
    )

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    lateinit var navController: TestNavHostController
    lateinit var mockViewModel: AppMainViewModel

    @Test
    fun test_AppMainNavigate_InitialDestination_Is_Home() {
        composeTestRule.onNodeWithText("홈").assertIsDisplayed()
    }

    @Test
    fun test_StartActivity_Navigate_to_ProfileScreen_back_to_HomeScreen() {
        composeTestRule.onNodeWithTag("go_to_profile").performClick()
        composeTestRule.onNodeWithText("프로필").assertIsDisplayed()
        composeTestRule.onNodeWithTag("go_to_home").performClick()
        composeTestRule.onNodeWithText("홈").assertIsDisplayed()
    }

}