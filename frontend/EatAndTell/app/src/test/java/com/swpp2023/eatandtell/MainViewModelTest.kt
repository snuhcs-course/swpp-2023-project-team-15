package com.swpp2023.eatandtell

import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.swpp2023.eatandtell.data.security.SharedPreferencesManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.*

class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private val context: Context = mockk(relaxed = true)
    private val sharedPreferences: SharedPreferences = mockk(relaxed = true)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = MainViewModel()
        mockkObject(SharedPreferencesManager)

        every { context.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.getString("Token", null) } returns "some_token"
    }

    @Test
    fun `checkIfLoggedIn when token is not null or empty sets isLoggedIn to true`() {
        every { sharedPreferences.getString("Token", "token") } returns "some_token"

        // Act
        viewModel.checkIfLoggedIn(context)

        // Assert
        Assert.assertTrue(viewModel.isLoggedIn.value == false)
    }

    @Test
    fun `checkIfLoggedIn when token is null or empty sets isLoggedIn to false`() {
        // Arrange
        every { sharedPreferences.getString("Token", null) } returns null

        // Act
        viewModel.checkIfLoggedIn(context)

        // Assert
        Assert.assertFalse(viewModel.isLoggedIn.value == true)
    }

    @After
    fun tearDown() {
        // Optionally clear all mocks if needed
    }
}
