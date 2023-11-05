package com.example.eatandtell.ui.appmain
import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import com.example.eatandtell.di.ApiService
import com.example.eatandtell.dto.RestReqDTO
import com.example.eatandtell.ui.start.MainCoroutineRule
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.ByteArrayInputStream

//class MainCoroutineRule:MainCoroutineRule
@ExtendWith(MockKExtension::class)
class AppMainViewModelTest {
    @MockK
    private val context: AppMainActivity = mockk(relaxed = true)
    val mockApiService = mockk<ApiService>()
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainCoroutineRule()
    private lateinit var viewModel: AppMainViewModel

    @Before
    fun setUp() {
        //MockKAnnotations.init(this)
        viewModel= AppMainViewModel(mockApiService)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        //mockkStatic(::showToast)
        //every {showToast(context,any())} returns
    }

    @Test
    fun prepareFileData_returnsBytes() = runTest {
        val mockUri: Uri = mockk()
        val mockContentResolver = mockk<ContentResolver>()
        val byteArray = "unit_test".toByteArray()
        val mockInputStream = ByteArrayInputStream(byteArray)

        every { context.contentResolver } returns mockContentResolver
        every { mockContentResolver.openInputStream(mockUri) } returns mockInputStream

        val bytes = viewModel.prepareFileData(mockUri, context)

        // Now you can assert that the bytes are not null and have the expected content
        assertNotNull(bytes)
        assertArrayEquals(byteArray, bytes)
    }
    // use RunTest to test suspend functions. See https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/
    @Test
    fun uploadPhotosAndPost_returnsNotNull() = runTest {
        val mockUri: Uri = mockk()
        val mockContentResolver = mockk<ContentResolver>()
        val byteArray = "unit_test".toByteArray()
        val mockInputStream = ByteArrayInputStream(byteArray)

        every { context.contentResolver } returns mockContentResolver
        every { mockContentResolver.openInputStream(mockUri) } returns mockInputStream

        val photoPath= listOf(mockUri)
        val rating ="3.0"
        val restaurant=RestReqDTO("example_rest")
        val description= "test"
        val gotToken = viewModel.uploadPhotosAndPost(photoPath, restaurant, rating,description, context )
        println("gotToken: $gotToken")
        assertNotNull(gotToken)
    }
    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }



/*
    @Test
    fun registerUser_meme_returnsTrue()= runTest {
        val gotToken =
        println("gotToken: $gotToken")
        assertNotNull(gotToken)
    }*/



}