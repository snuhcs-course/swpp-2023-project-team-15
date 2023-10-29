package com.example.eatandtell.ui.start

import android.util.Log
import com.example.eatandtell.ui.showToast
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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
    @Test
    fun loginUser_meme_returnsTrue() = runTest {
        // mock static method of Log. See https://stackoverflow.com/a/55251961
        val gotToken = viewModel.loginUser("meme", "meme", context)
        println("gotToken: $gotToken")
        assertNotNull(gotToken)
    }

    @Test
    fun registerUser_meme_returnsTrue()= runTest {
        val gotToken = viewModel.registerUser("meme3", "meme3", "meme3@gmail.com", context)
        println("gotToken: $gotToken")
        assertNotNull(gotToken)
    }
}