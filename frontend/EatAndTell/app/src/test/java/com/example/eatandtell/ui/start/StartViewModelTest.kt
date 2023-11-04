package com.example.eatandtell.ui.start

import RetrofitClient
import android.util.Log
import com.example.eatandtell.di.ApiService
import com.example.eatandtell.dto.RegisterRequest
import com.example.eatandtell.dto.RegisterResponse
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkObject
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
@ExtendWith(MockKExtension::class)
class StartViewModelTest {
    @MockK
    private val context: StartActivity = mockk(relaxed = true)

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    private lateinit var viewModel: StartViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel= StartViewModel()
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
        val gotToken = viewModel.loginUser("meme", "meme", context)
        println("gotToken: $gotToken")
        assertNotNull(gotToken)
    }

    //testing registerUser on a unit level can have side effects so mock API
    @Test
    fun registerUser_meme_returnsTrue()= runTest {
        val mockApiService: ApiService = mockk()
        val fakeResponse = RegisterResponse("success") // Create a fake response that the API would return
        val registerRequestData = RegisterRequest("username", "password", "email")
        mockkObject(RetrofitClient)
        every { RetrofitClient.retro.create(ApiService::class.java) } returns mockApiService


        // Assume that the response has a 'token' field
        coEvery { mockApiService.registerUser(registerRequestData) } returns fakeResponse


        // Act
        val resultToken = viewModel.registerUser("username", "password", "email", context)

        // Assert
        assertNotNull(resultToken) // Assuming the fakeResponse has a non-null token

        // Verify that the ApiService's registerUser method was called
        coVerify { mockApiService.registerUser(registerRequestData) }
    }
    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }
}