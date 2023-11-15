package com.example.eatandtell.ui.appmain
import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.eatandtell.data.repository.ApiRepository
import com.example.eatandtell.dto.ImageURLResponse
import com.example.eatandtell.dto.PhotoDTO
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.RestReqDTO
import com.example.eatandtell.dto.RestaurantDTO
import com.example.eatandtell.dto.UploadPostRequest
import com.example.eatandtell.dto.UserDTO
import com.example.eatandtell.ui.start.MainCoroutineRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.ByteArrayInputStream

//class MainCoroutineRule:MainCoroutineRule
@ExtendWith(MockKExtension::class)
@RunWith(RobolectricTestRunner::class)
class AppMainViewModelTest {
    @MockK
    private val context: AppMainActivity = mockk(relaxed = true)
    val mockRepository = mockk<ApiRepository>()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainCoroutineRule()
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: AppMainViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel= AppMainViewModel(mockRepository)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        //mockkStatic(::showToast)
        //every {showToast(context,any())} returns
    }

    @Test
    fun prepareFileData_returnsBytes() = runTest {
        val mockUri = Uri.parse("file:///path/to/resource")
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
    fun uploadPhotosAndPost_changes_uploadStatus_to_success() = runTest {
        @MockK
        val mockUri: Uri = mockk()
        val mockContentResolver = mockk<ContentResolver>()
        val byteArray = "unit_test".toByteArray()
        val mockInputStream = ByteArrayInputStream(byteArray)
        val photoPath= listOf(mockUri)
        val rating ="3.0"
        val restaurant=RestReqDTO("example_rest",1,"")
        val description= "test"
        val mockPostData= mockk<UploadPostRequest>()
        val postDTO=PostDTO(1, UserDTO(1,"test","test","", listOf("")), RestaurantDTO(1,"test"),"0","test",
            listOf(PhotoDTO(1,"",1,)),"",true,0,
            listOf("")
        )


        every { context.contentResolver } returns mockContentResolver
        every { mockContentResolver.openInputStream(mockUri) } returns mockInputStream
        coEvery { mockRepository.getImageURL(any(),any()) } returns Result.success(ImageURLResponse(""))
        coEvery { mockRepository.uploadPost(any(),any()) } returns Result.success(postDTO)
        viewModel.uploadPhotosAndPost(photoPath,restaurant, rating,description,context)
        advanceUntilIdle()
        val result= viewModel.uploadStatus.value


        assertEquals("포스트가 업로드되었습니다", result)

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