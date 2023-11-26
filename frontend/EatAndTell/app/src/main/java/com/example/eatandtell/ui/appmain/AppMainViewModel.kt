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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class AppMainViewModel@Inject constructor(private val apiRepository: ApiRepository) : ViewModel() {

    private var token: String? = null
    private val _uploadStatus = MutableLiveData<String>()
    val uploadStatus: LiveData<String> = _uploadStatus

    private val _editStatus = MutableLiveData<String>()
    val editStatus: LiveData<String> = _editStatus
    private val _tagUpdateStatus = MutableLiveData<String>()
    val tagUpdateStatus: LiveData<String> = _tagUpdateStatus

    val homePosts = mutableStateListOf<PostDTO>()
    val isLoading = mutableStateOf(true)
    val loadError = mutableStateOf<String?>(null)

    // Define state holders
    val profilePosts = mutableStateListOf<PostDTO>()
    val myInfo = mutableStateOf(UserInfoDTO(0, "", "", "", listOf(), false, 0, 0))
    val loading = mutableStateOf(false)

    var myProfile = UserDTO(0, "", "", "", listOf())

    var photoUris = mutableStateListOf<Uri>()// store image uri
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
            // Log the exception or handle it as needed
            null
        }
    }

    // Inside AppMainViewModel


    suspend fun loadHomePosts(selectedTab: String) {
            try {
                isLoading.value = true
                if (selectedTab == "추천") {
                    getPersonalizedPosts { fetchedPosts ->
                        homePosts.clear()
                        homePosts.addAll(fetchedPosts)
                    }
                } else {
                    getFollowingPosts { fetchedPosts ->
                        homePosts.clear()
                        homePosts.addAll(fetchedPosts)
                    }
                }
            } catch (e: Exception) {
                loadError.value = "홈 피드 로딩에 실패하였습니다"
            } finally {
                isLoading.value = false
            }
    }

    suspend fun loadProfileData(userId: Int?, loadType: Int, selectedTab: String) {
        try {
            isLoading.value = true
            when (loadType) {
                1 -> loadUserFeed(userId)
                2 -> if (selectedTab == "MY") loadUserFeed(userId) else loadLikedFeed()
            }
        } catch (e: Exception) {
            if (e !is CancellationException) {
                loadError.value = "유저 피드 로딩에 실패하였습니다"
            }
        } finally {
            isLoading.value = false
        }
    }

    // Load user feed
    suspend fun loadUserFeed(userId: Int?) {
            getUserFeed(
                userId = userId,
                onSuccess = { info, posts ->
                    myInfo.value = info
                    profilePosts.clear()
                    profilePosts.addAll(posts)
                }
            )
    }

    // Load user feed
    suspend fun loadLikedFeed() {
            getLikedFeed (
                onSuccess = { posts ->
                    profilePosts.clear()
                    profilePosts.addAll(posts)
                }
            )
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
                    myProfile = UserDTO(myProfile.id, myProfile.username, description, url, myProfile.tags) //프로필 편집 후 myProfile 업데이트
                } catch (e: Exception) {
                    // Handle exceptions, e.g., from network calls, here
                    if (e !is CancellationException) {
                        Log.d("edit profile error", e.message ?: "Network error")

                        _editStatus.postValue("프로필 편집에 실패했습니다")
                    } else {
                        Log.d("edit profile error", "cancellation exception")
//                        _editStatus.postValue("프로필이 편집되었습니다")
                        _editStatus.postValue("프로필 편집에 실패했습니다")

                    }
                }

        }

        private suspend fun uploadPost(postData: UploadPostRequest) {
            val authorization = "Token $token"
            val response= apiRepository.uploadPost(authorization, postData)
            response.onSuccess {

            }
            response.onFailure{e->
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
                throw message
            }

        }

    suspend fun getPersonalizedPosts(onSuccess: (List<PostDTO>) -> Unit) {
        val authorization = "Token $token"
        val response = apiRepository.getPersonalizedPosts(authorization)
        response.onSuccess { response ->
            onSuccess(response.data)

        }
        response.onFailure { message ->
            throw message
        }
    }

    suspend fun getFollowingPosts(onSuccess: (List<PostDTO>) -> Unit) {
        val authorization = "Token $token"
        val response = apiRepository.getFollowingPosts(authorization)
        response.onSuccess { response ->
            onSuccess(response.data)

        }
        response.onFailure { message ->
            throw message
        }
    }

        suspend fun getLikedFeed(onSuccess: (List<PostDTO>) -> Unit) {
            val authorization = "Token $token"
            val response = apiRepository.getLikedFeed(authorization)
            response.onSuccess{response->
                onSuccess(response)
            }
            response.onFailure {e->
                throw e
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
                updatePostInList(homePosts, post_id)
                updatePostInList(profilePosts, post_id)
                Log.d("toggle like", "success") }
            response.onFailure { e ->
                Log.d("toggle like error", e.message ?: "Network error")
            }
        }
    private fun updatePostInList(postsList: MutableList<PostDTO>, postId: Int) {
        val index = postsList.indexOfFirst { it.id == postId }
        if (index != -1) {
            val post = postsList[index]
            val updatedPost = post.copy(is_liked = !post.is_liked, like_count = post.like_count + if (post.is_liked) -1 else 1)
            postsList[index] = updatedPost
        }
    }

        suspend fun toggleFollow(user_id: Int):Boolean {
            val authorization = "Token $token"
            return try {
                val response = apiRepository.toggleFollow(authorization, user_id)
                Log.d("toggle follow", "success")
                true
            } catch (e: Exception) {
                Log.d("toggle follow error", e.message ?: "Network error")
                false
            }
        }



        suspend fun deletePost(post_id: Int) {
            val authorization = "Token $token"
            try {
                val response = apiRepository.deletePost(authorization, post_id)
                homePosts.removeAll { it.id == post_id }
                profilePosts.removeAll { it.id == post_id }
                Log.d("delete post", "success")
            } catch (e: Exception) {
                Log.d("delete post error", e.message ?: "Network error")
            }
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
                throw e
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
                throw e // rethrow the exception to be caught in the calling function
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
                throw e // rethrow the exception to be caught in the calling function
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
                throw e // rethrow the exception to be caught in the calling function
            }

        }

        suspend fun getSearchedRest(
            restaurantName: String,
            x: String? = null,
            y: String? = null
        ): List<SearchedRestDTO> {
            val authorization = "Token $token"
            val response = apiRepository.getSearchedRest(authorization, restaurantName, x, y)
            var result=listOf<SearchedRestDTO>()
            response.onSuccess { response ->
                Log.d("search rest", "success")
                result= response.data
            }
            response.onFailure {e->
                Log.d("search rest error", e.message ?: "Network error")
                result= listOf()
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
                    _tagUpdateStatus.postValue("태그가 업데이트되었습니다")
                }
                response.onFailure { e ->
                    Log.d("refresh tags error", e.message ?: "Network error")
                    _tagUpdateStatus.postValue("태그 업데이트에 실패하였습니다")
                }
            }
        }

    suspend fun getTopTags(onSuccess: (List<TopTag>) -> Unit, onError: (String) -> Unit) {
        val authorization = "Token $token"
        val response = apiRepository.getTopTags(authorization)
        response.onSuccess{response->
            onSuccess(response)
            Log.d("getTopTags", "success")
        }
        response.onFailure { e->
            val errorMessage = e.message ?: "Network error"
            Log.d("getTopTags error", errorMessage)
            onError(errorMessage)
        }

    }

    suspend fun getFollowers(user_id:Int?=null, onSuccess: (List<UserDTO>) -> Unit){
        val authorization = "Token $token"
        val response = apiRepository.getFollowers(authorization,user_id)
        response.onSuccess { response->
            onSuccess(response)
        }.onFailure { e->
            Log.d("getFollowers error", e.message ?: "Network error")
            throw e // rethrow the exception to be caught in the calling function
        }
    }

    suspend fun getFollowings(user_id:Int?=null, onSuccess: (List<UserDTO>) -> Unit){
        val authorization = "Token $token"
        val response = apiRepository.getFollowings(authorization,user_id)
        response.onSuccess { response->
            onSuccess(response)
        }.onFailure { e->
            Log.d("getFollowers error", e.message ?: "Network error")
            throw e // rethrow the exception to be caught in the calling function
        }
    }
}

// Event wrapper to handle one-time events


