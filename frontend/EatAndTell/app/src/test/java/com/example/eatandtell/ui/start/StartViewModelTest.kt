package com.example.eatandtell.ui.start

import android.util.Log
import com.example.eatandtell.data.api.ApiService
import com.example.eatandtell.di.RetrofitClient
import com.example.eatandtell.dto.LoginRequest
import com.example.eatandtell.dto.LoginResponse
import com.example.eatandtell.dto.RegisterRequest
import com.example.eatandtell.dto.RegisterResponse
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// required for coroutine test. See https://stackoverflow.com/a/71808251
@ExperimentalCoroutinesApi
class MainCoroutineRule(private val dispatcher: TestDispatcher = StandardTestDispatcher()) : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}

@UninstallModules(RetrofitClient::class)
@HiltAndroidTest
@ExtendWith(MockKExtension::class)
class StartViewModelTest {

    @MockK
    private val context: StartActivity = mockk(relaxed = true)
    var mockApiService=mockk<ApiService>(relaxed = true)



    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Before
    fun setUp() {

        MockKAnnotations.init(this)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0


        //mockkStatic("com.example.eatandtell.ui.showToast")
        //every { showToast(any(), any()) } just Runs
    }

    // use RunTest to test suspend functions. See https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/
    //loginUser has no side effects, so no need to mock API
    @Test
    fun loginUser_meme_returnsTrue() = runTest {
        // mock static method of Log. See https://stackoverflow.com/a/55251961
        val viewModel= StartViewModel(mockApiService)
        val fakeResponse = LoginResponse("success")
        val loginRequestData = LoginRequest("username", "password")

        coEvery { mockApiService.loginUser(loginRequestData) } returns fakeResponse
        val gotToken = viewModel.loginUser("meme", "meme", context)
        println("gotToken: $gotToken")
        assertNotNull(gotToken)
    }

    //testing registerUser on a unit level can have side effects so mock API
    @Test
    fun registerUser_meme_returnsTrue() = runTest {
        val viewModel= StartViewModel(mockApiService)
        // Define the behavior of your mock when specific functions are called
        val fakeResponse = RegisterResponse("success")
        val registerRequestData = RegisterRequest("username", "password", "email")

        // Setup the mock behavior for the ApiService
        coEvery { mockApiService.registerUser(registerRequestData) } returns fakeResponse

        // Call the method you're testing on your ViewModel
        val resultToken = viewModel.registerUser("username", "password", "email",context)

        // Assert the expected outcomes
        assertNotNull(resultToken)
        assertEquals("success", resultToken)
        println("gotToken: $resultToken")

        // Verify that the mocked API service was called
        coVerify { mockApiService.registerUser(registerRequestData) }
    }
    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }
}