package com.example.eatandtell.ui.appmain
import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import com.example.eatandtell.dto.RestReqDTO
import com.example.eatandtell.ui.start.MainCoroutineRule
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import java.io.InputStream

//class MainCoroutineRule:MainCoroutineRule
@ExtendWith(MockKExtension::class)
class AppMainViewModelTest {
    @MockK
    private val context: AppMainActivity = mockk(relaxed = true)
    @get:Rule
    val mainRule = MainCoroutineRule()
    private lateinit var viewModel: AppMainViewModel

    @Before
    fun setUp() {
        //MockKAnnotations.init(this)
        viewModel= AppMainViewModel()
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        //mockkStatic(::showToast)
        //every {showToast(context,any())} returns
    }

    @Test
    fun prepareFileData_returnsBytes()= runTest {
        val mockUri1: Uri = mockk<Uri>()
        val photoPath= listOf(mockUri1)
        val mockContentResolver = mockk<ContentResolver>()
        val mockInputStream = mockk<InputStream>()
        every { mockContentResolver.openInputStream(any()) } returns mockInputStream
        every{mockInputStream.readBytes()} returns "unit_test".toByteArray()
        val Bytes= viewModel.prepareFileData(mockUri1,context)
        assertNotNull(Bytes)
    }
    // use RunTest to test suspend functions. See https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/
    @Test
    fun uploadPhotosAndPost_returnsNotNull() = runTest {
        val mockUri1: Uri = mockk<Uri>()
        val mockContentResolver = mockk<ContentResolver>()
        val mockInputStream = mockk<InputStream>()
        every { mockContentResolver.openInputStream(any()) } returns mockInputStream
        every{mockInputStream.readBytes()} returns "unit_test".toByteArray()
        Log.d("Err_msg", "here")

        val photoPath= listOf(mockUri1)
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