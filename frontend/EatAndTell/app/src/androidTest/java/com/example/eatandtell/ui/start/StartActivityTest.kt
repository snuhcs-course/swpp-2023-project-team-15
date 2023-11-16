package com.example.eatandtell.ui.start

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<StartActivity>()
    var hiltRule= HiltAndroidRule(this)

    lateinit var navController: TestNavHostController
    lateinit var mockViewModel: StartViewModel

    @Test
    fun test_StartActivity_InitialDestination_IsLogin() {
        composeTestRule.onNodeWithTag("go_to_signup").assertIsDisplayed()
    }
    @Test
    fun testLoginScreen_withValidCredentials_navigatesToHome() {
//        // Start the login activity
//        ActivityScenario.launch(StartActivity::class.java)
//
//        // Use Espresso to interact with UI elements
//        onView(withId(R.id.username)).perform(typeText("testUser"))
//        onView(withId(R.id.password)).perform(typeText("testPass"))
//        onView(withId(R.id.login_button)).perform(click())
//
//        // Assertions to check if navigation to the home screen is successful
//        // ...
    }

    @Test
    fun test_StartActivity_NavigateToSignup() {
        composeTestRule.onNodeWithTag("go_to_signup").performClick()
        composeTestRule.onNodeWithTag("go_to_login").assertIsDisplayed()
    }
}