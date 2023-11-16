package com.example.eatandtell.ui.appmain
import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.eatandtell.data.repository.ApiRepository
import com.example.eatandtell.dto.GetAllPostsResponse
import com.example.eatandtell.dto.GetFeedResponse
<<<<<<< HEAD
=======
import com.example.eatandtell.dto.GetSearchedRestResponse
>>>>>>> 709c5836bc5afa955d335b107d001a1a038ece39
import com.example.eatandtell.dto.ImageURLResponse
import com.example.eatandtell.dto.PhotoDTO
import com.example.eatandtell.dto.PhotoReqDTO
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.RestReqDTO
import com.example.eatandtell.dto.RestaurantDTO
import com.example.eatandtell.dto.SearchedRestDTO
import com.example.eatandtell.dto.TagsDTO
import com.example.eatandtell.dto.TopTag
import com.example.eatandtell.dto.UploadPostRequest
import com.example.eatandtell.dto.UserDTO
<<<<<<< HEAD
import com.example.eatandtell.dto.toggleFollowResponse
=======
import com.example.eatandtell.dto.UserInfoDTO
import com.example.eatandtell.dto.toggleFollowResponse
import com.example.eatandtell.dto.toggleLikeResponse
>>>>>>> 709c5836bc5afa955d335b107d001a1a038ece39
import com.example.eatandtell.ui.start.MainCoroutineRule
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
import java.io.FileNotFoundException

//class MainCoroutineRule:MainCoroutineRule
@ExtendWith(MockKExtension::class)
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
    fun initialize_triggersProfileFetch() = runTest {
        val expectedResponse = GetFeedResponse(
            id = 1,
            username = "username",
            description = "description",
            avatar_url = "avatarUrl",
//            tags = listOf("tag1", "tag2"),
            tags = listOf(),
            is_followed = false,
            follower_count = 10,
            following_count = 5,
            posts = listOf() // Assume an empty list for simplicity
        )
        val mockUserDTO = UserDTO(expectedResponse.id, expectedResponse.username, expectedResponse.description, expectedResponse.avatar_url, expectedResponse.tags)
        // Mock the API call to get the user's profile
        coEvery { mockRepository.getMyFeed(any()) } returns Result.success(expectedResponse)

        // Call initialize with any token
        viewModel.initialize("testToken")

        // Advance time until all coroutines are completed
        advanceUntilIdle()

        // Verify that the user's profile is fetched and set correctly
        assertEquals(mockUserDTO, viewModel.myProfile)
    }





    @Test
    fun prepareFileData_returnsBytes() = runTest {
        @MockK
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
    fun prepareFileData_fileNotFound_returnsNull() = runTest {
        val mockUri: Uri = mockk()
        val mockContentResolver = mockk<ContentResolver>()

        // Simulate a scenario where the file associated with the URI cannot be found
        every { context.contentResolver } returns mockContentResolver
        every { mockContentResolver.openInputStream(mockUri) } throws FileNotFoundException("File not found")

        // Invoke prepareFileData with the mock URI
        val result = viewModel.prepareFileData(mockUri, context)

        // Assert that the method returns null when the file is not found
        assertNull(result)
    }

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

    @Test
    fun uploadPhotosAndPost_generalException_updatesUploadStatusWithError() = runTest {
        val mockUri: Uri = mockk()
        val byteArray = "unit_test".toByteArray()
        val mockInputStream = ByteArrayInputStream(byteArray)
        val photoPaths = listOf(mockUri)
        val fakeException = Exception("Upload failed")

        every { context.contentResolver.openInputStream(mockUri) } returns mockInputStream
        coEvery { mockRepository.getImageURL(any(), any()) } throws fakeException

        viewModel.uploadPhotosAndPost(photoPaths, RestReqDTO("test", 1, ""), "3.0", "test description", context)
        advanceUntilIdle()

        assertEquals("이미지 업로드에 실패했습니다.", viewModel.uploadStatus.value)
    }





    @Test
    fun uploadPhotosAndEditProfile_changes_editStatus_to_success() = runTest {
        @MockK
        val mockUri: Uri = mockk()
        val mockContentResolver = mockk<ContentResolver>()
        val byteArray = "unit_test".toByteArray()
        val mockInputStream = ByteArrayInputStream(byteArray)
        val photoPath= listOf(mockUri)
        val userDTO= UserDTO(1,"test","test","", listOf(""))



        every { context.contentResolver } returns mockContentResolver
        every { mockContentResolver.openInputStream(mockUri) } returns mockInputStream
        coEvery { mockRepository.getImageURL(any(),any()) } returns Result.success(ImageURLResponse(""))
        coEvery { mockRepository.editProfile(any(),any()) } returns Result.success(userDTO)
        viewModel.uploadPhotosAndEditProfile(photoPath," ", context, "")
        advanceUntilIdle()
        val result= viewModel.editStatus.value


        assertEquals("프로필이 편집되었습니다", result)

    }

<<<<<<< HEAD
    @Test
    fun getAllPosts_should_return_posts()= runTest{
        val mockPosts=listOf(PostDTO(1, UserDTO(1,"test","test","", listOf("")), RestaurantDTO(1,"test"),"0","test",
            listOf(PhotoDTO(1,"",1,)),"",true,0,
            listOf("")
        ))
        coEvery { mockRepository.getAllPosts(any()) } returns Result.success(GetAllPostsResponse( mockPosts))
        var result:List<PostDTO>?=null
        viewModel.getAllPosts { posts->
            result=posts
        }
        assertEquals(mockPosts,result)
    }

    @Test
    fun getLikedFeed_should_return_posts()= runTest{
        val mockPosts=listOf(PostDTO(1, UserDTO(1,"test","test","", listOf("")), RestaurantDTO(1,"test"),"0","test",
            listOf(PhotoDTO(1,"",1,)),"",true,0,
            listOf("")
        ))
        coEvery { mockRepository.getLikedFeed(any()) } returns Result.success( mockPosts)
        var result:List<PostDTO>?=null
        viewModel.getLikedFeed { posts->
            result=posts
        }
        assertEquals(mockPosts,result)
    }

    @Test
    fun getUserFeed_for_user1_should_return_posts()= runTest{
        val mockPosts=listOf(PostDTO(1, UserDTO(1,"test","test","", listOf("")), RestaurantDTO(1,"test"),"0","test",
            listOf(PhotoDTO(1,"",1,)),"",true,0,
            listOf("")
        ))
        val userdto=UserDTO(1,"","","", listOf(""))
        coEvery { mockRepository.getUserFeed(any(),1) } returns Result.success( GetFeedResponse( 1,"","","",listOf(""),false,0,0,mockPosts))
        var result:List<PostDTO>?=null
        viewModel.getUserFeed(1) { user,posts->
            result=posts
        }
        assertEquals(mockPosts,result)
    }

    @Test
    fun toggleFollow_returns_true() = runTest{
        coEvery { mockRepository.toggleFollow(any(),any()) } returns Result.success(
            toggleFollowResponse("")
        )
        val result= viewModel.toggleFollow(1)
        assertEquals(result,true)
=======
//    @Test
//    fun uploadPhotosAndEditProfile_uploadFails_updatesEditStatusWithError() = runTest {
//        val mockUri: Uri = mockk()
//        val byteArray = "unit_test".toByteArray()
//        val mockInputStream = ByteArrayInputStream(byteArray)
//        val photoPaths = listOf(mockUri)
//
//        every { context.contentResolver.openInputStream(mockUri) } returns mockInputStream
//        coEvery { mockRepository.getImageURL(any(), any()) } throws Exception("Upload failed")
//
//        viewModel.uploadPhotosAndEditProfile(photoPaths, "test description", context, "original_avatar_url")
//        advanceUntilIdle()
//
//        assertEquals("프로필 편집에 실패했습니다", viewModel.editStatus.value)
//    }

    @Test
    fun uploadPhotosAndEditProfile_profileEditFails_updatesEditStatusWithError() = runTest {
        val mockUri: Uri = mockk(relaxed = true)
        val photoPaths = listOf(mockUri)
        val fakeException = Exception("Profile edit failed")

        // Mocking the file preparation and image upload to succeed
        every { context.contentResolver.openInputStream(mockUri) } returns ByteArrayInputStream(ByteArray(1024))
        coEvery { mockRepository.getImageURL(any(), any()) } returns Result.success(ImageURLResponse("fake_image_url"))

        // Mocking editProfile to throw an exception
        coEvery { mockRepository.editProfile(any(), any()) } throws fakeException

        viewModel.uploadPhotosAndEditProfile(photoPaths, "Test description", context, "original_avatar_url")
        advanceUntilIdle()

        assertEquals("프로필 편집에 실패했습니다", viewModel.editStatus.value)
    }

    @Test
    fun uploadPhotosAndEditProfile_imageUploadFails_updatesEditStatusWithError() = runTest {
        val mockUri: Uri = mockk(relaxed = true)
        val photoPaths = listOf(mockUri)
        val fakeException = Exception("Image upload failed")

        // Mocking the file preparation to succeed
        every { context.contentResolver.openInputStream(mockUri) } returns ByteArrayInputStream(ByteArray(1024))

        // Mocking getImageURL to throw an exception
        coEvery { mockRepository.getImageURL(any(), any()) } throws fakeException

        viewModel.uploadPhotosAndEditProfile(photoPaths, "Test description", context, "original_avatar_url")
        advanceUntilIdle()

        assertEquals("프로필 편집에 실패했습니다", viewModel.editStatus.value)
    }


    @Test
    fun uploadPhotosAndEditProfile_editProfileFails_updatesEditStatusWithError() = runTest {
        val mockUri: Uri = mockk(relaxed = true)
        val byteArray = "unit_test".toByteArray()
        val mockInputStream = ByteArrayInputStream(byteArray)
        val photoPaths = listOf(mockUri)
        val fakeException = Exception("Profile edit failed")

        // Mocking the file preparation and image upload to succeed
        every { context.contentResolver.openInputStream(mockUri) } returns mockInputStream
        coEvery { mockRepository.getImageURL(any(), any()) } returns Result.success(ImageURLResponse("fake_image_url"))

        // Mocking the profile editing to fail
        coEvery { mockRepository.editProfile(any(), any()) } throws fakeException

        viewModel.uploadPhotosAndEditProfile(photoPaths, "test description", context, "original_avatar_url")
        advanceUntilIdle()

        assertEquals("프로필 편집에 실패했습니다", viewModel.editStatus.value)
    }




    @Test
    fun getMyProfile_success_updates_myProfile() = runTest {
        val expectedResponse = GetFeedResponse(
            id = 1,
            username = "username",
            description = "description",
            avatar_url = "avatarUrl",
            tags = listOf("tag1", "tag2"),
            is_followed = false,
            follower_count = 10,
            following_count = 5,
            posts = listOf() // Assume an empty list for simplicity
        )
        coEvery { mockRepository.getMyFeed(any()) } returns Result.success(expectedResponse)

        viewModel.getMyProfile()
        advanceUntilIdle()

        with(viewModel.myProfile) {
            assertEquals(expectedResponse.id, id)
            assertEquals(expectedResponse.username, username)
            assertEquals(expectedResponse.description, description)
            assertEquals(expectedResponse.avatar_url, avatar_url)
        }
    }

    @Test
    fun `getMyProfile on API failure logs error and throws exception`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        val exception = Exception(errorMessage)

        // Mock the apiRepository to return a failure
        coEvery { mockRepository.getMyFeed(any()) } returns Result.failure(exception)

        // Act & Assert
        try {
            viewModel.getMyProfile()
            advanceUntilIdle()
            fail("Exception should have been thrown")
        } catch (e: Exception) {
            // Verify that the caught exception is the same as what was thrown
            assertEquals(errorMessage, e.message)

            // Verify that the error log is called
            verify { Log.d("getMyProfile error", errorMessage) }
        }
    }


    @Test
    fun toggleLike_success_logsSuccessMessage() = runTest {
        val postId = 123
        val successResponse = toggleLikeResponse("Success Message")

        // Mock the API call to return a successful result
        coEvery { mockRepository.toggleLike(any(), postId) } returns Result.success(successResponse)

        viewModel.toggleLike(postId)

        // Verify that the success log is called
        verify { Log.d("toggle like", "success") }
    }

    @Test
    fun toggleLike_failure_logsErrorMessage() = runTest {
        val postId = 123
        val errorMessage = "Network error"
        val exception = Exception(errorMessage)

        // Mock the API call to return a failure
        coEvery { mockRepository.toggleLike(any(), postId) }returns Result.failure(exception)

        viewModel.toggleLike(postId)

        // Verify that the error log is called with the appropriate message
        verify { Log.d("toggle like error", errorMessage) }
    }


    @Test
    fun toggleFollow_success_returnsTrueAndLogsSuccess() = runTest {
        val userId = 123
        val successResponse = toggleFollowResponse("Success Message")

        // Mock the API call to return a successful result
        coEvery { mockRepository.toggleFollow(any(), userId) } returns Result.success(successResponse)

        val result = viewModel.toggleFollow(userId)

        // Verify that the function returns true for a successful operation
        assertTrue(result)

        // Verify that the success log is called
        verify { Log.d("toggle follow", "success") }
    }


    @Test
    fun toggleFollow_failure_logsErrorMessage() = runTest {
        val userId = 123
        val exception = RuntimeException("Network error")

        // Mock the API call to return a failure
        coEvery { mockRepository.toggleFollow(any(), userId)  } throws exception

        val result = viewModel.toggleFollow(userId)

        assertFalse(result)
        // Verify that the error log is called with the appropriate message
        coVerify { Log.d("toggle follow error", "Network error") }
    }

    @Test
    fun deletePost_success_logsSuccess() = runTest {
        val postId = 123
        val deletedPost = PostDTO(
            id = postId,
            user = UserDTO(1, "user", "desc", "url", listOf()),
            restaurant = RestaurantDTO(1, "restaurant"),
            rating = "4.5",
            description = "Nice place",
            photos = listOf(),
            created_at = "2022-01-01",
            is_liked = false,
            like_count = 10,
            tags = listOf("tag1", "tag2")
        )

        coEvery { mockRepository.deletePost(any(), postId) } returns Result.success(deletedPost)

        viewModel.deletePost(postId)
        advanceUntilIdle()

        verify { Log.d("delete post", "success") }
    }

    @Test
    fun deletePost_failure_logsError() = runTest {
        val postId = 123
        val exceptionMessage = "Network error"
        val exception = Exception(exceptionMessage)

        // Mock the API call to simulate a failure and throw an exception
        coEvery { mockRepository.deletePost(any(), postId) }throws exception

        viewModel.deletePost(postId)
        advanceUntilIdle()

        // Verify that the error log is called with the expected message
        verify { Log.d("delete post error", exceptionMessage) }
>>>>>>> 709c5836bc5afa955d335b107d001a1a038ece39
    }



<<<<<<< HEAD






=======
    @Test
    fun getAllPosts_success_invokesOnSuccessWithCorrectData() = runTest {
        val mockPosts = listOf(
            PostDTO(1, UserDTO(1, "user1", "desc1", "url1", listOf()), RestaurantDTO(1, "restaurant1"), "3.0", "test description", listOf(PhotoDTO(1, "photoUrl", 1)), "2023-01-01", true, 10, listOf("tag1", "tag2")),
            PostDTO(2, UserDTO(2, "user2", "desc2", "url2", listOf()), RestaurantDTO(2, "restaurant2"), "4.0", "another test description", listOf(PhotoDTO(2, "anotherPhotoUrl", 2)), "2023-01-02", false, 5, listOf("tag3", "tag4"))
        )
        val mockResponse = GetAllPostsResponse(mockPosts)
        val resultResponse = Result.success(mockResponse)

        // Mock the API call
        coEvery { mockRepository.getAllPosts(any()) } returns resultResponse

        // Create a variable to capture the onSuccess callback result
        var onSuccessResult: List<PostDTO>? = null

        // Call the function
        viewModel.getAllPosts { posts ->
            onSuccessResult = posts
        }

        // Advance time until all coroutines are completed
        advanceUntilIdle()

        // Assert that the onSuccess callback was called with the correct data
        assertNotNull(onSuccessResult)
        assertEquals(mockPosts, onSuccessResult)
    }

    @Test(expected = Exception::class)
    fun getAllPosts_failure_throwsException() = runTest {
        val exception = Exception("Network error")
        coEvery { mockRepository.getAllPosts(any()) } returns Result.failure(exception)

        viewModel.getAllPosts { }

        // The test will fail if the exception is not thrown
    }

    @Test
    fun getLikedFeed_success_callsOnSuccessWithCorrectData() = runTest {
        val mockPosts = listOf(
            PostDTO(1, UserDTO(1, "user1", "desc1", "url1", listOf()), RestaurantDTO(1, "restaurant1"), "3.0", "test description", listOf(PhotoDTO(1, "photoUrl", 1)), "2023-01-01", true, 10, listOf("tag1", "tag2")),
            PostDTO(2, UserDTO(2, "user2", "desc2", "url2", listOf()), RestaurantDTO(2, "restaurant2"), "4.0", "another test description", listOf(PhotoDTO(2, "anotherPhotoUrl", 2)), "2023-01-02", false, 5, listOf("tag3", "tag4"))
        )
        coEvery { mockRepository.getLikedFeed(any()) } returns Result.success(mockPosts)

        var result: List<PostDTO>? = null
        viewModel.getLikedFeed { posts ->
            result = posts
        }

        // Verify that the onSuccess lambda was called with the correct data
        assertNotNull(result)
        assertEquals(mockPosts, result)
    }

    @Test(expected = Exception::class)
    fun getLikedFeed_failure_throwsException() = runTest {
        val exception = Exception("Network error")
        coEvery { mockRepository.getLikedFeed(any()) } returns Result.failure(exception)

        viewModel.getLikedFeed { }

        // The test will fail if the exception is not thrown
    }

    @Test
    fun getUserFeed_withUserId_callsOnSuccessWithCorrectData() = runTest {
        val mockUserId = 123
        val mockPostDTOList = listOf(PostDTO(1, UserDTO(1, "username", "desc", "avatar", listOf()), RestaurantDTO(1, "restName"), "3.0", "desc", listOf(PhotoDTO(1, "url", 1)), "createdAt", true, 10, listOf()))
        val mockUserInfoDTO = UserInfoDTO(123, "username", "description", "avatarUrl", listOf("tag1"), false, 10, 5)
        val expectedFeedResponse = GetFeedResponse(123, "username", "description", "avatarUrl", listOf("tag1"), false, 10, 5,mockPostDTOList)

        // Mock the API call to get the user feed
        coEvery { mockRepository.getUserFeed(any(), eq(mockUserId)) } returns Result.success(expectedFeedResponse)

        var resultUserInfoDTO: UserInfoDTO? = null
        var resultPostDTOList: List<PostDTO>? = null

        viewModel.getUserFeed(mockUserId) { userInfoDTO, postDTOList ->
            resultUserInfoDTO = userInfoDTO
            resultPostDTOList = postDTOList
        }

        advanceUntilIdle()

        assertEquals(mockUserInfoDTO, resultUserInfoDTO)
        assertEquals(mockPostDTOList, resultPostDTOList)
    }

    @Test(expected = Exception::class)
    fun getUserFeed_failure_throwsException() = runTest {
        val exception = Exception("Network error")
        coEvery { mockRepository.getLikedFeed(any()) } returns Result.failure(exception)

        viewModel.getUserFeed(123) { _, _ -> }
        // The test will fail if the exception is not thrown
    }



    @Test
    fun getFilteredUsersByName_success_callsOnSuccessWithCorrectData() = runTest {
        val mockUsername = "testUser"
        val mockUserList = listOf(UserDTO(1, "testUser", "description", "avatarUrl", listOf()))
        coEvery { mockRepository.getFilteredUsersByName(any(), eq(mockUsername)) } returns Result.success(mockUserList)

        var onSuccessCalled = false
        val onSuccess: (List<UserDTO>) -> Unit = { users ->
            onSuccessCalled = true
            assertEquals(mockUserList, users)
        }

        viewModel.getFilteredUsersByName(mockUsername, onSuccess)

        assertTrue(onSuccessCalled)
    }

    @Test
    fun getFilteredUsersByName_failure_logsAndThrowsException() = runTest {
        val mockUsername = "testUser"
        val exception = Exception("Network error")
        coEvery { mockRepository.getFilteredUsersByName(any(), eq(mockUsername)) } returns Result.failure(exception)

        var exceptionThrown: Exception? = null
        val onSuccess: (List<UserDTO>) -> Unit = {}

        try {
            viewModel.getFilteredUsersByName(mockUsername, onSuccess)
        } catch (e: Exception) {
            exceptionThrown = e
        }

        assertNotNull(exceptionThrown)
        assertEquals("Network error", exceptionThrown?.message)
    }

    @Test
    fun getFilteredUsersByTag_success_callsOnSuccessWithCorrectData() = runTest {
        val mockTag = "testTag"
        val mockUserList = listOf(
            UserDTO(1, "user1", "desc1", "avatarUrl1", listOf()),
            UserDTO(2, "user2", "desc2", "avatarUrl2", listOf())
        )

        // Mock the API call to return the mock user list
        coEvery { mockRepository.getFilteredUsersByTag(any(), eq(mockTag)) } returns Result.success(mockUserList)

        var result: List<UserDTO>? = null
        viewModel.getFilteredUsersByTag(mockTag) {
            result = it
        }

        // Verify that the onSuccess lambda was called with the correct data
        assertEquals(mockUserList, result)
    }


//    @Test(expected = Exception::class)
//    fun getFilteredUsersByTag_failure_throwsException() = runTest {
//        val mockTag = "testTag"
//        val exception = Exception("Network error")
//
//        // Mock the API call to throw an exception
//        coEvery { mockRepository.getFilteredUsersByTag(any(), eq(mockTag)) } returns Result.failure(exception)
//
//        viewModel.getFilteredUsersByTag(mockTag) {
//            // This block should not be called
//            fail("onSuccess should not be called")
//        }
//
//        // The test expects an exception to be thrown
//    }
    @Test
    fun getFilteredUsersByTag_failure_logsAndThrowsException() = runTest {
        val mockTag = "testTag"
        val exception = Exception("Network error")

        // Mock the API call to simulate a failure
        coEvery { mockRepository.getFilteredUsersByTag(any(), eq(mockTag)) } returns Result.failure(exception)

        var exceptionThrown: Exception? = null
        val onSuccess: (List<UserDTO>) -> Unit = {}

        try {
            viewModel.getFilteredUsersByTag(mockTag, onSuccess)
        } catch (e: Exception) {
            exceptionThrown = e
        }

        assertNotNull(exceptionThrown)
        assertEquals("Network error", exceptionThrown?.message)
    }


    @Test
    fun getFilteredByRestaurants_success_callsOnSuccessWithCorrectData() = runTest {
        val mockPosts = listOf(
            PostDTO(1, UserDTO(1, "user1", "desc1", "avatarUrl1", listOf()), RestaurantDTO(1, "restaurant1"), "rating1", "description1", listOf(), "timestamp1", true, 10, listOf()),
            PostDTO(2, UserDTO(2, "user2", "desc2", "avatarUrl2", listOf()), RestaurantDTO(2, "restaurant2"), "rating2", "description2", listOf(), "timestamp2", false, 5, listOf())
        )
        val expectedResponse = GetAllPostsResponse(mockPosts)
        val restaurantName = "testRestaurant"

        // Mock the API call
        coEvery { mockRepository.getFilteredByRestaurants(any(), eq(restaurantName)) } returns Result.success(expectedResponse)

        // Create a variable to capture the onSuccess result
        var onSuccessResult: List<PostDTO>? = null

        // Call the function
        viewModel.getFilteredByRestaurants(restaurantName) {
            onSuccessResult = it
        }

        // Verify that onSuccess was called with the correct data
        assertNotNull(onSuccessResult)
        assertEquals(mockPosts, onSuccessResult)
    }


    @Test
    fun getFilteredByRestaurants_failure_logsAndThrowsException() = runTest {
        val restaurantName = "testRestaurant"
        val exception = Exception("Network error")

        // Mock the API call to simulate a failure
        coEvery { mockRepository.getFilteredByRestaurants(any(), eq(restaurantName)) } returns Result.failure(exception)

        var exceptionThrown: Exception? = null
        val onSuccess: (List<PostDTO>) -> Unit = {}

        try {
            viewModel.getFilteredByRestaurants(restaurantName, onSuccess)
        } catch (e: Exception) {
            exceptionThrown = e
        }

        assertNotNull(exceptionThrown)
        assertEquals("Network error", exceptionThrown?.message)
    }

    @Test
    fun getSearchedRest_returnsExpectedResults() = runTest {
        val restaurantName = "Test Restaurant"
        val mockSearchedRestDTOList = listOf(
            SearchedRestDTO(1, "Rest1", "Seoul", "interior","1","2"),
            SearchedRestDTO(2, "Rest2", "Seoul", "interior","1","2")
        )
        val expectedResponse = GetSearchedRestResponse(mockSearchedRestDTOList)
        // Mock the API call
        coEvery { mockRepository.getSearchedRest(any(), eq(restaurantName), any(), any()) } returns Result.success(expectedResponse)

        // Invoke getSearchedRest
        val result = viewModel.getSearchedRest(restaurantName)

        // Verify that the result matches the expected list
        assertEquals(mockSearchedRestDTOList, result)
    }

    @Test
    fun getSearchedRest_onFailure_returnsEmptyList() = runTest {
        val restaurantName = "Test Restaurant"

        // Mock the API call to simulate a failure
        coEvery { mockRepository.getSearchedRest(any(), eq(restaurantName), any(), any()) } returns Result.failure(RuntimeException("Network error"))

        // Invoke getSearchedRest
        val result = viewModel.getSearchedRest(restaurantName)

        // Verify that the result is an empty list
        assertTrue(result.isEmpty())
    }


    @Test
    fun refreshTags_success_updatesTagListAndStatus() = runTest {
        val mockTags = listOf("tag1", "tag2", "tag3")
        val mockResponse = mockk<TagsDTO>()
        every { mockResponse.user_tags } returns mockTags
        coEvery { mockRepository.refreshTags(any()) } returns Result.success(mockResponse)

        val onSuccessMock: (List<String>) -> Unit = mockk(relaxed = true)

        viewModel.refreshTags(onSuccessMock, context)
        advanceUntilIdle()

        // Verify that onSuccess is called with the correct data
        verify { onSuccessMock(mockTags) }

        // Verify that the live data is updated with the success message
        assertEquals("태그가 업데이트되었습니다", viewModel.tagUpdateStatus.value)
    }

    @Test
    fun refreshTags_failure_updatesStatusWithError() = runTest {
        val fakeException = Exception("Network error")
        coEvery { mockRepository.refreshTags(any()) } returns Result.failure(fakeException)

        val onSuccessMock: (List<String>) -> Unit = mockk(relaxed = true)

        viewModel.refreshTags(onSuccessMock, context)
        advanceUntilIdle()

        // Verify that onSuccess is not called
        verify(exactly = 0) { onSuccessMock(any()) }

        // Verify that the live data is updated with the error message
        assertEquals("태그 업데이트에 실패하였습니다", viewModel.tagUpdateStatus.value)
    }


    @Test
    fun getTopTags_success_invokesOnSuccess() = runTest {
        val mockTopTags = listOf(TopTag("Tag1_ko","Tag1_en","Type"), TopTag("Tag2_ko","Tag2_en","Type") )

        // Mock the API call to return a successful response
        coEvery { mockRepository.getTopTags(any()) } returns Result.success(mockTopTags)

        var onSuccessInvoked = false
        var receivedTags: List<TopTag>? = null

        viewModel.getTopTags(
            onSuccess = { tags ->
                onSuccessInvoked = true
                receivedTags = tags
            },
            onError = { _ -> }
        )

        // Assert that onSuccess was invoked with the correct data
        assertTrue(onSuccessInvoked)
        assertEquals(mockTopTags, receivedTags)
    }

    @Test
    fun getTopTags_failure_invokesOnError() = runTest {
        val errorMessage = "Network error"

        // Mock the API call to return a failure
        coEvery { mockRepository.getTopTags(any()) } returns Result.failure(Exception(errorMessage))

        var onErrorInvoked = false
        var receivedErrorMessage: String? = null

        viewModel.getTopTags(
            onSuccess = { _ -> },
            onError = { error ->
                onErrorInvoked = true
                receivedErrorMessage = error
            }
        )

        // Assert that onError was invoked with the correct message
        assertTrue(onErrorInvoked)
        assertEquals(errorMessage, receivedErrorMessage)
    }
>>>>>>> 709c5836bc5afa955d335b107d001a1a038ece39



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