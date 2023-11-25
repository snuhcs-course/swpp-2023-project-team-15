package com.example.eatandtell.ui.appmain

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
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

    @Test
    fun test_postReview() {
        composeTestRule.onNodeWithTag("go_to_upload").performClick()
        composeTestRule.onAllNodesWithText("리뷰 작성").onFirst().assertExists()

        composeTestRule.onNodeWithText("식당 검색").performClick()
        composeTestRule.onNodeWithText("맛집 검색").assertIsDisplayed()

        // todo: search for a restaurant and select it, and then navigate to the review screen, and then post a review
    }


    @Test
    fun test_refreshTag() {
        composeTestRule.onNodeWithTag("go_to_profile").performClick()
        composeTestRule.onRoot().printToLog(tag = "test")
        composeTestRule.onNodeWithText("태그 갱신").assertIsDisplayed()

        // todo: leave a review, click on the refresh tag button and assert that the tag is updated accordingly
    }

    @Test
    fun test_likePostOnHome() {
        composeTestRule.onRoot().printToLog(tag = "test")
        // wait 10sec
        Thread.sleep(1000)

        composeTestRule.onAllNodesWithTag("heart_full").onFirst().assertDoesNotExist()
        composeTestRule.onAllNodesWithTag("heart_empty").onFirst().assertExists()
        composeTestRule.onAllNodesWithTag("heart_empty").onFirst().performClick()
        composeTestRule.onAllNodesWithTag("heart_full").onFirst().assertExists()
        Thread.sleep(1000)
        // todo: test that the liked post is displayed in the profile screen

        composeTestRule.onAllNodesWithTag("heart_full").onFirst().performClick()
        composeTestRule.onAllNodesWithTag("heart_empty").onFirst().assertExists()
        composeTestRule.onAllNodesWithTag("heart_full").onFirst().assertDoesNotExist()

    }

    private fun waitUntilNodeWith(tag: String? = null, text: String? = null) {
        if(tag != null) {
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
            }
        } else if (text != null) {
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
            }
        }
    }
    @Test
    fun test_followingUserOnHome() {
        composeTestRule.onRoot().printToLog(tag = "test")

        // 1. move to profile screen
        waitUntilNodeWith(tag = "profile_row")
        composeTestRule.onAllNodesWithTag("profile_row").onFirst().assertExists()
        composeTestRule.onAllNodesWithTag("profile_row").onFirst().performClick()

        // 2. click follow button
        waitUntilNodeWith(text = "팔로우하기")
        composeTestRule.onNodeWithText("팔로우하기").performClick()


        // todo: 3. test that the following user's post is displayed in the following tab

        // 4. click unfollow button (twice)
        // Note: this part is flaky. sometimes the test fails even if the unfollow button is actually displayed.
        waitUntilNodeWith(tag = "unfollow_button")
        composeTestRule.onNodeWithTag("unfollow_button").performClick()

        waitUntilNodeWith(text = "팔로우 취소")
        composeTestRule.onNodeWithText("팔로우 취소").performClick()

        waitUntilNodeWith(text = "팔로우하기")
        composeTestRule.onNodeWithText("팔로우하기").assertIsDisplayed()
    }
}