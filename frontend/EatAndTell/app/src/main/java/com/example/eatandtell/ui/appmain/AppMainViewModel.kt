package com.example.eatandtell.ui.appmain

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatandtell.data.repository.ApiRepository
import com.example.eatandtell.dto.EditProfileRequest
import com.example.eatandtell.dto.PhotoReqDTO
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.RestReqDTO
import com.example.eatandtell.dto.SearchedRestDTO
import com.example.eatandtell.dto.TopTag
import com.example.eatandtell.dto.UploadPostRequest
import com.example.eatandtell.dto.UserDTO
import com.example.eatandtell.dto.UserInfoDTO
import com.example.eatandtell.ui.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class AppMainViewModel @Inject constructor(private val apiRepository: ApiRepository) : ViewModel() {

    private var token: String? = null
    private val _uploadStatus = MutableLiveData<String>()
    val uploadStatus: LiveData<String> = _uploadStatus

    private val _editStatus = MutableLiveData<String>()
    val editStatus: LiveData<String> = _editStatus
    private val _tagUpdateStatus = MutableStateFlow<String?>(null)
    val tagUpdateStatus = _tagUpdateStatus.asStateFlow()

    private val _homePosts = MutableStateFlow<List<PostDTO>>(listOf())
    val homePosts = _homePosts.asStateFlow()
    private val _loadError = MutableStateFlow<String?>(null)
    val loadError = _loadError.asStateFlow()

    private val _profilePosts = MutableStateFlow<List<PostDTO>>(listOf())
    val profilePosts = _profilePosts.asStateFlow()

    private val _myInfo = MutableStateFlow(UserInfoDTO(0, "", "", "", listOf(), false, 0, 0))
    val myInfo = _myInfo.asStateFlow()


    private val _editProfileLoading = MutableStateFlow(false)
    val editProfileLoading = _editProfileLoading.asStateFlow()

    private val _homeLoading = MutableStateFlow(true)
    val homeLoading = _homeLoading.asStateFlow()

    private val _profileLoading = MutableStateFlow(true)
    val profileLoading = _profileLoading.asStateFlow()

    private val _searchLoading = MutableStateFlow(true)
    val searchLoading = _searchLoading.asStateFlow()

    private val _userInfo = MutableStateFlow(UserInfoDTO(0, "", "", "", listOf(), false, 0, 0))
    val userInfo = _userInfo.asStateFlow()
    private val _userPosts = MutableStateFlow<List<PostDTO>>(listOf())
    val userPosts = _userPosts.asStateFlow()


    private val _followers = MutableStateFlow<List<UserDTO>>(emptyList())
    val followers: StateFlow<List<UserDTO>> = _followers.asStateFlow()

    private val _followings = MutableStateFlow<List<UserDTO>>(emptyList())
    val followings: StateFlow<List<UserDTO>> = _followings.asStateFlow()


    // SearchScreen

    private val _userLists = MutableStateFlow<List<UserDTO>>(emptyList())
    val userLists = _userLists.asStateFlow()
    private val _userListsByTags = MutableStateFlow<List<UserDTO>>(emptyList())
    val userListsByTags = _userListsByTags.asStateFlow()
    private val _postLists = MutableStateFlow<List<PostDTO>>(listOf())
    val postLists = _postLists.asStateFlow()
    private val _topTags = MutableStateFlow<List<TopTag>>(emptyList())
    val topTags = _topTags.asStateFlow()

    // Adding LiveData for error messages
    private val _searchError = MutableLiveData<String?>()
    val searchError: LiveData<String?> = _searchError
    var myProfile = UserDTO(0, "", "", "", listOf())

    var photoUris = mutableStateListOf<Uri>()
        // store image uri
        private set
    val reviewDescription = mutableStateOf("")
    fun initialize(token: String?) {
        this.token = token
        viewModelScope.launch {
            getMyProfile()
        }
    }

    fun prepareFileData(photoPath: Uri, context: Context): ByteArray? {
        val contentResolver = context.contentResolver
        return try {
            contentResolver.openInputStream(photoPath)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) {
            // Log the exception or handle it here
            null
        }
    }

    // Inside AppMainViewModel


    suspend fun loadHomePosts(selectedTab: String) {
        try {
            _homeLoading.value = true
            if (selectedTab == "추천") {
                getPersonalizedPosts { fetchedPosts ->
                    _homePosts.value = fetchedPosts
                }
            } else {
                getFollowingPosts { fetchedPosts ->
                    _homePosts.value = fetchedPosts
                }
            }
        } catch (e: Exception) {
            _loadError.value = "홈 피드 로딩에 실패하였습니다"
        } finally {
            _homeLoading.value = false
        }
    }

    // Inside AppMainViewModel

    suspend fun loadUserPosts(userId: Int?) {
        try {
            _profileLoading.value = true // Start loading
            getUserFeed(
                userId = userId,
                onSuccess = { userInfo, userPosts ->
                    _userInfo.value = userInfo // Update user information
                    _userPosts.value = userPosts // Update user posts
                }
            )
        } catch (e: Exception) {
            if (e !is CancellationException) {
                _loadError.value = "유저 피드 로딩에 실패하였습니다"
            }
        } finally {
            _profileLoading.value = false // End loading
        }
    }


    suspend fun loadProfileData(userId: Int?, loadType: Int, selectedTab: String) {
        try {
            _profileLoading.value = true // Update StateFlow
            when (loadType) {
                1 -> loadUserFeed(userId)
                2 -> if (selectedTab == "MY") loadUserFeed(userId) else loadLikedFeed()
            }
        } catch (e: Exception) {
            if (e !is CancellationException) {
                _loadError.value = "유저 피드 로딩에 실패하였습니다"
            }
        } finally {
            _profileLoading.value = false
        }
    }

    // Load user feed
    private suspend fun loadUserFeed(userId: Int?) {
        getUserFeed(
            userId = userId,
            onSuccess = { info, posts ->
                _myInfo.value = info
                _profilePosts.value = posts
            }
        )
    }

    // Load user feed
    private suspend fun loadLikedFeed() {
        getLikedFeed(
            onSuccess = { posts ->
                _profilePosts.value = posts
            }
        )
    }

    fun resetLoadError() {
        _loadError.value = null
    }

    fun resetUploadStatus() {
        _uploadStatus.value = null
    }

    // Perform search based on selected type and search text
    suspend fun performSearch(searchText: String, selectedButton: String) {
        _searchLoading.value = true
        try {
            if (searchText.isNotEmpty()) {
                _postLists.value = emptyList() // Clear post list
                _userLists.value = emptyList() // Clear user list
                _userListsByTags.value = emptyList() // Clear user list by tags
                when (selectedButton) {
                    "유저" -> handleUserSearch(searchText)
                    "태그" -> handleTagSearch(searchText)
                    "식당" -> handleRestaurantSearch(searchText)
                }
            }
        } catch (e: CancellationException) {
            Log.d("Search", "검색 작업이 취소되었습니다: ${e.message}")
            throw e
        } catch (e: Exception) {
            Log.e("Search Error", "Failed to load search results: ${e.message}")
            _loadError.value = "search 로딩에 실패하였습니다"
        } finally {
            _searchLoading.value = false
        }
    }

    fun clearSearchError() {
        _searchError.value = null
    }

    private suspend fun handleUserSearch(searchText: String) {
        val response = apiRepository.getFilteredUsersByName("Token $token", searchText)
        response.onSuccess { users ->
            _userLists.value = users
        }
        response.onFailure { e ->
            Log.e("Search Error", "User search failed: ${e.message}")
            throw e
        }
        _searchLoading.value = false
    }


    private suspend fun handleTagSearch(searchText: String) {
        val response = apiRepository.getFilteredUsersByTag("Token $token", searchText)
        response.onSuccess { users ->
            _userListsByTags.value = users
        }
        response.onFailure { e ->
            throw e
        }
        _searchLoading.value = false
    }

    private suspend fun handleRestaurantSearch(searchText: String) {
        val response = apiRepository.getFilteredByRestaurants("Token $token", searchText)
        response.onSuccess { posts ->
            _postLists.value = posts.data
        }
        response.onFailure { e ->
            Log.e("Search Error", "Restaurant search failed: ${e.message}")
            throw e
        }
        _searchLoading.value = false
    }

    fun fetchTopTags() {
        viewModelScope.launch {
            val response = apiRepository.getTopTags("Token $token")
            response.onSuccess { tags ->
                _topTags.value = tags.take(5) // Update the MutableStateFlow
            }
            response.onFailure { e ->
                Log.e("Tags fetching Error", "Failed to load top tags: ${e.message}")
                _tagUpdateStatus.value = "태그 로딩에 실패하였습니다"
            }
        }
    }


    fun uploadPhotosAndPost(
        photoPaths: List<Uri>,
        restaurant: RestReqDTO,
        rating: String,
        description: String,
        context: Context
    ) {


        viewModelScope.launch {

            val photoUrls = mutableListOf<String>()
            val photoByteArrays = photoPaths.mapNotNull { prepareFileData(it, context) }
            for (byteArray in photoByteArrays) {
                val requestBody: RequestBody =
                    byteArray.toRequestBody("image/*".toMediaTypeOrNull())
                val fileToUpload: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "image",
                    "this_name_does_not_matter.jpg",
                    requestBody
                )

                try {
                    val imageUrl = getImageURL(fileToUpload)
                    photoUrls.add(imageUrl)

                } catch (e: Exception) {
                    Log.d("Image Upload Error", e.message ?: "Upload failed")
                    showToast(context, "이미지 업로드에 실패했습니다.")
                    _uploadStatus.postValue("이미지 업로드에 실패했습니다.")
                    return@launch
                }
            }
            try {
                Log.d("upload photos and post", "trying")
                val photos = photoUrls.map { PhotoReqDTO(it) }
                val postData = UploadPostRequest(
                    restaurant = restaurant,
                    photos = photos,
                    rating = rating,
                    description = description
                )
                uploadPost(postData)
                Log.d("upload photos and post", "success")
                _uploadStatus.postValue("포스트가 업로드되었습니다")


            } catch (e: CancellationException) {
                Log.d("upload photos and post error", "cancellation exception")
            } catch (e: Exception) {
                Log.d("upload photos and post error", e.message ?: "Network error")
                _uploadStatus.postValue("포스트 업로드에 실패했습니다.")
            }
            photoUris.clear()
            reviewDescription.value = ""

        }
    }


    suspend fun uploadPhotosAndEditProfile(

        photoPaths: List<Uri>, //실제로는 length 1짜리
        description: String,
        context: Context,
        org_avatar_url: String,
    ) {
        _editProfileLoading.value = true


        Log.d("edit profile photoPaths: ", photoPaths.toString())
        val photoUrls = mutableListOf<String>()
        val photoByteArrays = photoPaths.mapNotNull { prepareFileData(it, context) }
        for (byteArray in photoByteArrays) {
            val requestBody: RequestBody =
                byteArray.toRequestBody("image/*".toMediaTypeOrNull())
            val fileToUpload: MultipartBody.Part = MultipartBody.Part.createFormData(
                "image",
                "this_name_does_not_matter.jpg",
                requestBody
            )
            try {
                val imageUrl = getImageURL(fileToUpload)
                photoUrls.add(imageUrl)
            } catch (e: Exception) {
                Log.d("Image Upload Error", e.message ?: "Upload failed")
                _editStatus.postValue("프로필 편집에 실패했습니다")
            }
        }

        try {
            Log.d("edit profile", description)
            Log.d("edit profile", photoUrls.toString())
            Log.d("edit profile", org_avatar_url)
            val url = if (photoUrls.isEmpty()) org_avatar_url else photoUrls[0]
            val profileData =
                EditProfileRequest(description = description, avatar_url = url)
            Log.d("edit profile", profileData.toString())
            editProfile(profileData)
            _editStatus.postValue("프로필이 편집되었습니다")
            myProfile = UserDTO(
                myProfile.id,
                myProfile.username,
                description,
                url,
                myProfile.tags
            ) //프로필 편집 후 myProfile 업데이트
        } catch (e: Exception) {
            // Handle exceptions, e.g., from network calls, here
            if (e !is CancellationException) {
                Log.d("edit profile error", e.message ?: "Network error")

                _editStatus.postValue("프로필 편집에 실패했습니다")
            } else {
                Log.d("edit profile error", "cancellation exception")
                _editStatus.postValue("프로필 편집에 실패했습니다")
            }
        }
        finally {
            _editProfileLoading.value = false
        }

    }

    private suspend fun uploadPost(postData: UploadPostRequest) {
        val authorization = "Token $token"
        val response = apiRepository.uploadPost(authorization, postData)
        response.onSuccess {

        }
        response.onFailure { e ->
            val errorMessage = e.message ?: "Network error"
            Log.d("upload post error", errorMessage)
            throw e // rethrow the exception to be caught in the calling function

        }

    }

    private suspend fun editProfile(profileData: EditProfileRequest) {
        val authorization = "Token $token"

        try {
            apiRepository.editProfile(authorization, profileData)
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Network error"
            Log.d("edit profile error", errorMessage)
            throw e // rethrow the exception to be caught in the calling function
        }
    }

    private suspend fun getImageURL(fileToUpload: MultipartBody.Part?): String {
        val authorization = "Token $token"
        val response = apiRepository.getImageURL(
            authorization,
            fileToUpload
        ) // Assuming this is a suspend function call
        var get_img_url: String = ""
        response.onSuccess { response ->
            get_img_url = response.image_url
        }
        response.onFailure { exception ->
            val errorMessage = exception.message ?: "Network error"
            Log.d("image_url_error", errorMessage)
            get_img_url = errorMessage
            throw exception // rethrow the exception to be caught in the calling function
        }
        return get_img_url
    }

    suspend fun getAllPosts(onSuccess: (List<PostDTO>) -> Unit) {
        val authorization = "Token $token"
        val response = apiRepository.getAllPosts(authorization)
        response.onSuccess { response ->
            onSuccess(response.data)

        }
        response.onFailure { message ->
            _loadError.value = "전체 피드 로딩에 실패하였습니다"
        }

    }

    suspend fun getPersonalizedPosts(onSuccess: (List<PostDTO>) -> Unit) {
        val authorization = "Token $token"
        val response = apiRepository.getPersonalizedPosts(authorization)
        response.onSuccess { response ->
            onSuccess(response.data)

        }
        response.onFailure { message ->
            _loadError.value = "추천 피드 로딩에 실패하였습니다"
        }
    }

    suspend fun getFollowingPosts(onSuccess: (List<PostDTO>) -> Unit) {
        val authorization = "Token $token"
        val response = apiRepository.getFollowingPosts(authorization)
        response.onSuccess { response ->
            onSuccess(response.data)

        }
        response.onFailure {
            _loadError.value = "팔로잉 피드 로딩에 실패하였습니다"
        }
    }

    suspend fun getLikedFeed(onSuccess: (List<PostDTO>) -> Unit) {
        val authorization = "Token $token"
        val response = apiRepository.getLikedFeed(authorization)
        response.onSuccess { response ->
            onSuccess(response)
        }
        response.onFailure { e ->
            _loadError.value = "좋아요 피드 로딩에 실패하였습니다"
        }

    }


    suspend fun getUserFeed(
        userId: Int? = null,
        onSuccess: (UserInfoDTO, List<PostDTO>) -> Unit
    ) {
        val authorization = "Token $token"

        val response = (
                if (userId != null) apiRepository.getUserFeed(authorization, userId)
                else apiRepository.getMyFeed(authorization)
                )
        response.onSuccess { response ->

            println("user feed response is")
            println(response.tags)
            val myInfo = UserInfoDTO(
                id = response.id,
                username = response.username,
                description = response.description,
                avatar_url = response.avatar_url,
                tags = response.tags,
                is_followed = response.is_followed,
                follower_count = response.follower_count,
                following_count = response.following_count,
            )
            val myPosts = response.posts ?: listOf() //posts가 null이라서 임시처리
            println("get user feed success")
            onSuccess(myInfo, myPosts)

        }
        response.onFailure { e ->
            print("get user feed error")
            println(e.message ?: "Network error")
            throw e // rethrow the exception to be caught in the calling function
        }

    }


    suspend fun toggleLike(post_id: Int) {
        val authorization = "Token $token"
        val response = apiRepository.toggleLike(authorization, post_id)
        response.onSuccess {
            // Update post in all relevant lists
            updatePostInList(_homePosts, post_id)
            updatePostInList(_profilePosts, post_id)
            updatePostInList(_userPosts, post_id)
            Log.d("toggle like", "success")
        }
        response.onFailure { e ->
            Log.d("toggle like error", e.message ?: "Network error")
        }
    }

    private fun updatePostInList(postsFlow: MutableStateFlow<List<PostDTO>>, postId: Int) {
        val updatedPosts = postsFlow.value.map { post ->
            if (post.id == postId) post.copy(
                is_liked = !post.is_liked,
                like_count = post.like_count + if (post.is_liked) -1 else 1
            ) else post
        }
        postsFlow.value = updatedPosts
    }


    suspend fun toggleFollow(userId: Int): Boolean {
        val authorization = "Token $token"
        val response = apiRepository.toggleFollow(authorization, userId)
        response.onSuccess {
            val updatedUserInfo = userInfo.value.copy(
                is_followed = !userInfo.value.is_followed,
                follower_count = userInfo.value.follower_count + if (userInfo.value.is_followed) -1 else 1
            )
            _userInfo.value =
                updatedUserInfo // Assuming _userInfo is the MutableStateFlow backing userInfo StateFlow
            Log.d("toggle follow", "success")
            return true
        }
        response.onFailure { e ->
            Log.d("toggle follow error", e.message ?: "Network error")
            _loadError.value = "팔로우에 실패하였습니다. 잠시 후 다시 시도해주세요."
        }
        return false
    }


    suspend fun deletePost(postId: Int): Boolean {
        val authorization = "Token $token"
        val result = apiRepository.deletePost(authorization, postId)
        if(result.isFailure) {
            Log.d("delete post error", result.exceptionOrNull()?.message ?: "Network error")
            _loadError.value = "포스트 삭제에 실패하였습니다. 잠시 후 다시 시도해주세요."
            return false
        }
        val updatedHomePosts = homePosts.value.filter { it.id != postId }
        _homePosts.value = updatedHomePosts
        val updatedProfilePosts = profilePosts.value.filter { it.id != postId }
        _profilePosts.value = updatedProfilePosts
        Log.d("delete post", "success")
        return true
    }

    suspend fun getMyProfile() {
        val authorization = "Token $token"

        val response = apiRepository.getMyFeed(authorization)

        response.onSuccess { response ->

            val myProf = UserDTO(
                response.id,
                response.username,
                response.description,
                response.avatar_url,
                listOf()
            )
            val myFoll = listOf(
                response.follower_count,
                response.following_count,
            )
            Log.d("getMyProfile", "success")

            myProfile = myProf
        }
        response.onFailure { e ->
            Log.d("getMyProfile error", e.message ?: "Network error")
            _loadError.value = "프로필 로딩에 실패하였습니다. 잠시 후 다시 시도해주세요."
        }

    }


    suspend fun getFilteredUsersByName(username: String, onSuccess: (List<UserDTO>) -> Unit) {
        val authorization = "Token $token"
        val response = apiRepository.getFilteredUsersByName(authorization, username)

        response.onSuccess { response ->
            //print response
            println(response)
            onSuccess(response)
            Log.d("getFilteredUsersByName", "success")
        }

        response.onFailure { e ->
            Log.d("getFilteredUsersByName error", e.message ?: "Network error")
            _loadError.value = "유저 검색에 실패하였습니다. 잠시 후 다시 시도해주세요."
        }

    }

    suspend fun getFilteredUsersByTag(tag: String, onSuccess: (List<UserDTO>) -> Unit) {
        val authorization = "Token $token"
        val response = apiRepository.getFilteredUsersByTag(authorization, tag)
        response.onSuccess { response ->
            onSuccess(response)
            Log.d("getFilteredUsersByTag", "success")
        }
        response.onFailure { e ->
            Log.d("getFilteredUsersByTag error", e.message ?: "Network error")
            _loadError.value = "유저 검색에 실패하였습니다. 잠시 후 다시 시도해주세요."
        }

    }

    suspend fun getFilteredByRestaurants(
        restaurantName: String,
        onSuccess: (List<PostDTO>) -> Unit
    ) {
        val authorization = "Token $token"
        val response = apiRepository.getFilteredByRestaurants(authorization, restaurantName)

        response.onSuccess { response ->
            onSuccess(response.data)
            Log.d("getFilteredByRestaurants", "success")
        }
        response.onFailure { e ->
            Log.d("getFilteredByRestaurants error", e.message ?: "Network error")
            _loadError.value = "식당 검색에 실패하였습니다. 잠시 후 다시 시도해주세요."
        }

    }

    suspend fun getSearchedRest(
        restaurantName: String,
        x: String? = null,
        y: String? = null
    ): List<SearchedRestDTO> {
        val authorization = "Token $token"
        val response = apiRepository.getSearchedRest(authorization, restaurantName, x, y)
        var result = listOf<SearchedRestDTO>()
        response.onSuccess { response ->
            Log.d("search rest", "success")
            result = response.data
        }
        response.onFailure { e ->
            Log.d("search rest error", e.message ?: "Network error")
            result = listOf()
        }
        return result

    }

    suspend fun refreshTags(onSuccess: (List<String>) -> Unit, context: Context) {
        val authorization = "Token $token"
        viewModelScope.launch {
            val response = apiRepository.refreshTags(authorization)
            response.onSuccess { response ->
                onSuccess(response.user_tags)
                Log.d("refresh tags", "success")
                _tagUpdateStatus.value = "태그가 업데이트되었습니다"
            }
            response.onFailure { e ->
                Log.d("refresh tags error", e.message ?: "Network error")
                _loadError.value = "태그갱신에 실패하였습니다. 잠시 후 다시 시도해주세요"
            }
        }
    }

    suspend fun getTopTags(onSuccess: (List<TopTag>) -> Unit, onError: (String) -> Unit) {
        val authorization = "Token $token"
        val response = apiRepository.getTopTags(authorization)
        response.onSuccess { response ->
            onSuccess(response)
            Log.d("getTopTags", "success")
        }
        response.onFailure { e ->
            val errorMessage = e.message ?: "Network error"
            Log.d("getTopTags error", errorMessage)
            onError(errorMessage)
        }

    }

    suspend fun getFollowers(userId: Int? = null) {
        val authorization = "Token $token"
        val response = apiRepository.getFollowers(authorization, userId)
        response.onSuccess { users ->
            _followers.value = users
        }.onFailure { e ->
            Log.e("getFollowers error", e.message ?: "Network error")
            _loadError.value = "팔로워 로딩에 실패하였습니다. 잠시 후 다시 시도해주세요."
        }
    }

    suspend fun getFollowings(userId: Int? = null) {
        val authorization = "Token $token"
        val response = apiRepository.getFollowings(authorization, userId)
        response.onSuccess { users ->
            _followings.value = users
        }.onFailure { e ->
            Log.e("getFollowings error", e.message ?: "Network error")
            _loadError.value = "팔로잉 로딩에 실패하였습니다. 잠시 후 다시 시도해주세요."
        }
    }

}



