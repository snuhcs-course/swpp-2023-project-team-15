package com.swpp2023.eatandtell.ui.start

import android.util.Log
import com.swpp2023.eatandtell.data.repository.ApiRepository
import com.swpp2023.eatandtell.data.repository.TokenRepository
import com.swpp2023.eatandtell.di.NetworkModule
import com.swpp2023.eatandtell.dto.LoginRequest
import com.swpp2023.eatandtell.dto.LoginResponse
import com.swpp2023.eatandtell.dto.RegisterRequest
import com.swpp2023.eatandtell.dto.RegisterResponse
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.MockKAnnotations
import io.mockk.coEvery
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
import kotlinx.coroutines.test.advanceUntilIdle
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

@UninstallModules(NetworkModule::class)
@HiltAndroidTest
@ExtendWith(MockKExtension::class)
class StartViewModelTest {

    @MockK
    private val context: StartActivity = mockk(relaxed = true)
    private val mockRepository=mockk<ApiRepository>(relaxed = true)
    private val mockTokenRepository=mockk<TokenRepository>(relaxed = true)


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
    fun loginUser_meme_updates_state_to_Success() = runTest {
        // mock static method of Log. See https://stackoverflow.com/a/55251961
        val viewModel= StartViewModel(mockRepository,mockTokenRepository)
        val fakeResponse = LoginResponse("success")
        val loginRequestData = LoginRequest("username", "password")

        coEvery { mockRepository.loginUser(loginRequestData) } returns Result.success(fakeResponse)
        viewModel.loginUser("username", "password", context)
        advanceUntilIdle()



        // Assert that the login state is updated to success
        assertTrue(viewModel.loginState.value is LoginState.Success)
    }

    @Test
    fun loginUser_error_updates_state_to_Error() = runTest {
        val viewModel = StartViewModel(mockRepository, mockTokenRepository)
        val loginRequestData = LoginRequest("username", "wrongpassword")

        val fakeException = Exception("Login failed")
        coEvery { mockRepository.loginUser(loginRequestData) } returns Result.failure(fakeException)

        viewModel.loginUser("username", "wrongpassword", context)
        advanceUntilIdle()

        assertTrue(viewModel.loginState.value is LoginState.Error)
        assertEquals("Login failed", (viewModel.loginState.value as LoginState.Error).message)
    }


    //testing registerUser on a unit level can have side effects so mock API
    @Test
    fun registerUser_meme_returnsTrue() = runTest {
        val viewModel = StartViewModel(mockRepository, mockTokenRepository)
        // Define the behavior of your mock when specific functions are called
        val fakeResponse = RegisterResponse("success")
        val registerRequestData = RegisterRequest("username", "password", "email")

        // Setup the mock behavior for the ApiService
        coEvery { mockRepository.registerUser(registerRequestData) } returns Result.success(
            fakeResponse
        )


        viewModel.registerUser("username", "password", "email", context)
        advanceUntilIdle()

        assert(viewModel.registerState.value is RegisterState.Success)
    }

    @Test
    fun registerUser_error_updates_state_to_Error() = runTest {
        val viewModel = StartViewModel(mockRepository, mockTokenRepository)
        val registerRequestData = RegisterRequest("username", "password", "email")

        val fakeException = Exception("Registration failed")
        coEvery { mockRepository.registerUser(registerRequestData) } returns Result.failure(fakeException)

        viewModel.registerUser("username", "password", "email", context)
        advanceUntilIdle()

        assertTrue(viewModel.registerState.value is RegisterState.Error)
        assertEquals("Registration failed", (viewModel.registerState.value as RegisterState.Error).message)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }
}