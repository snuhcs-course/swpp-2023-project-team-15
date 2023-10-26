package com.example.eatandtell.ui.start

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class StartViewModelTest {
    @Rule
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var context: StartActivity

    private var viewModel: StartViewModel? = null
    @Before
    fun setUp() {
        viewModel = StartViewModel()
    }

    @Test
    fun loginUser_meme_returnsTrue() {
        var gotToken : String? = null
        //GIVEN input username meme, password meme
        //WHEN login user is called
        viewModel!!.loginUser("meme", "meme", context) { token ->
            //THEN return response that is true
            gotToken = token
        }
        assertNotNull(gotToken)
    }

    @Test
    fun registerUser() {
    }
}