package com.example.eatandtell.ui.appmain
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
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



    var myProfile = UserDTO(0, "", "", "", listOf())

    var photoUris = mutableStateListOf<Uri>()// store image uri
    private set
    suspend fun initialize(token: String?) {
        this.token = token
        getMyProfile()
    }
    private val _tagUpdateStatus = MutableLiveData<String>()
    val tagUpdateStatus: LiveData<String> = _tagUpdateStatus



        //private val apiService = RetrofitClient.retro.create(ApiService::class.java)

        fun prepareFileData(photoPath: Uri, context: Context): ByteArray? {
            val contentResolver = context.contentResolver
            contentResolver.openInputStream(photoPath)?.use { inputStream ->
                return inputStream.readBytes()
            }
            return null
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


            }
        }

        fun uploadPhotosAndEditProfile(
            photoPaths: List<Uri>, //실제로는 length 1짜리
            description: String,
            context: Context,
            org_avatar_url: String,
        ) {
            viewModelScope.launch {

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
                    val imageUrl = getImageURL(fileToUpload)
                    photoUrls.add(imageUrl)
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
                    showToast(context, "프로필이 편집되었습니다")
                } catch (e: Exception) {
                    // Handle exceptions, e.g., from network calls, here
                    if (e !is CancellationException) {
                        Log.d("edit profile error", e.message ?: "Network error")
                        //showToast(context, "프로필 편집에 실패했습니다")
                        _editStatus.postValue("프로필 편집에 실패했습니다")
                    } else {
                        Log.d("edit profile error", "cancellation exception")
                        _editStatus.postValue("프로필이 편집되었습니다")
                        //TOOD: navigate을 해버리니까 cancellation 에러가 뜸. 그렇다고 navigate을 코루틴 내에서 화면이 너무 안 넘어가서 버튼을 연타하게 됨
                    }
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
            response.onSuccess { Log.d("toggle like", "success") }
            response.onFailure { e ->
                Log.d("toggle like error", e.message ?: "Network error")
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
                Log.d("delete post", "success")
            } catch (e: Exception) {
                Log.d("delete post error", e.message ?: "Network error")
            }
        }

        suspend fun getMyProfile() {
            val authorization = "Token $token"

            val response = apiRepository.getMyFeed(authorization)

            response.onSuccess { response ->
                val myInfo = UserDTO(
                    response.id,
                    response.username,
                    response.description,
                    response.avatar_url,
                    listOf()
                )

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
    }

// Event wrapper to handle one-time events


