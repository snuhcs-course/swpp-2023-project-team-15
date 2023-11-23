package com.example.eatandtell.ui.appmain

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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

    @Test
    fun test_searchUserByUserName() {
        // Navigate to the search screen
        composeTestRule.onNodeWithTag("go_to_search").performClick()

        // Focus on the search bar and input text
        composeTestRule.onNodeWithTag("search_bar_text_field").performClick()
        composeTestRule.onNodeWithTag("search_bar_text_field").performTextInput("jc")

        // Wait for the search results to appear
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            // Assuming "profile_row" is the tag for each user profile row in the search results
            composeTestRule.onAllNodesWithTag("profile_row").fetchSemanticsNodes().isNotEmpty()
        }

        // Assert that at least one search result is displayed
        composeTestRule.onAllNodesWithTag("profile_row").onFirst().assertExists()
        // todo: click on the first search result and navigate to the profile screen
    }
}