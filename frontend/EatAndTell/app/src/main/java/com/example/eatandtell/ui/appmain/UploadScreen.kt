// SignUpScreen.kt
package com.example.eatandtell.ui.appmain

import android.R.attr.path
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eatandtell.dto.PhotoReqDTO
import com.example.eatandtell.dto.RestReqDTO
import com.example.eatandtell.dto.UploadPostRequest
import com.example.eatandtell.ui.DraggableStarRating
import com.example.eatandtell.ui.MainButton
import com.example.eatandtell.ui.MediumWhiteButton
import com.example.eatandtell.ui.PostImage
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.WhiteTextField
import com.example.eatandtell.ui.showToast
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.InputStream


@Composable
fun UploadScreen(navController: NavHostController, context: ComponentActivity, viewModel: AppMainViewModel) {

    var restaurantName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var reviewDescription by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var myRating by rememberSaveable { mutableStateOf("0") }


    val profileUrl = "https://newprofilepic.photo-cdn.net//assets/images/article/profile.jpg?90af0c8"
//    val photoUrls = listOf(
//        "https://api.nudge-community.com/attachments/339560",
//        "https://img.siksinhot.com/place/1650516612762055.jpg?w=560&h=448&c=Y",
//        "https://img1.daumcdn.net/thumb/R800x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FdKS0uX%2FbtrScbvc9HH%2F5I2m53vgz0LWvszHQ9PQNk%2Fimg.jpg"
//    ) //TODO: get from gallery

    var photoPaths by remember { mutableStateOf(listOf<Uri>()) } //핸드폰 내의 파일 경로

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            photoPaths = it
        }


    val username = "Joshua-i"
    val userDescription = "고독한 미식가"

    // Main content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        // Profile Row
        Profile(profileUrl, username, userDescription);

        Spacer(modifier = Modifier.height(16.dp))

        // Images Row
        Row(
            modifier = Modifier
                .height(150.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            for (photoPath in photoPaths) {
                PostImage(photoPath.toString())
            }
        }

        // Medium White Button
        Spacer(modifier = Modifier.height(16.dp))
        MediumWhiteButton(onClick = { galleryLauncher.launch("image/*") }, text = "사진 추가하기")

        // Restaurant Name Text Field and Rating
        Spacer(modifier = Modifier.height(16.dp))

        Row (
            horizontalArrangement = Arrangement.SpaceBetween, //너비에 상관없이 양쪽 끝에 배치
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            WhiteTextField(
                value = restaurantName.text,
                onValueChange = { restaurantName = TextFieldValue(it) },
                placeholder = "맛집명",
                modifier = Modifier
                    .border(
                        width = 0.5.dp,
                        color = Color(0xFFC5C5C5),
                        shape = RoundedCornerShape(size = 4.dp)
                    )
                    .height(IntrinsicSize.Min)
                    .width(160.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            DraggableStarRating(currentRating = myRating.toInt(), onRatingChanged = {myRating = it.toString()} )
        }

        // Review Text Field
        Spacer(modifier = Modifier.height(12.dp))
        WhiteTextField(value = reviewDescription.text,
            onValueChange = { reviewDescription = TextFieldValue(it)},
            placeholder = "리뷰를 작성해 주세요",
            modifier = Modifier
                .border(
                    width = 0.5.dp,
                    color = Color(0xFFC5C5C5),
                    shape = RoundedCornerShape(size = 4.dp)
                )
                .fillMaxWidth()
                .weight(1f) // 남은 세로 길이 모두 리뷰 공간으로 할당
        )

        // Upload Button
        Spacer(modifier = Modifier.height(16.dp))
        UploadButton(
            viewModel = viewModel,
            restaurant = RestReqDTO(name = restaurantName.text),
            photoPaths = photoPaths ,
            rating = myRating,
            description = reviewDescription.text,
            context = context,
            onClick = {
                navigateToDestination(navController, "Home")
            }
        )

        Spacer(modifier = Modifier.height(20.dp))
    }

}


@Composable
fun UploadButton(viewModel: AppMainViewModel,
                 restaurant : RestReqDTO,
                 photoPaths: List<Uri>,
                 rating: String,
                 description: String,
                 context: Context,
                 onClick: () -> Unit) {
    var photoUrls = listOf<String>() // 실제 서버에 업로드할 주소

    val onClickReal = {
        when {
            restaurant.name.isBlank() -> showToast(context, "맛집명을 입력해주세요")
            description.isBlank() -> showToast(context, "리뷰를 입력해주세요")
            else -> {
                for(photoPath in photoPaths) {
                    //change photoPath in to photo with formData type
                    val contentResolver: ContentResolver = context.contentResolver
                    val inputStream: InputStream? = contentResolver.openInputStream(photoPath)
                    val byteArray: ByteArray? = inputStream?.readBytes()
                    val requestBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), byteArray!!)
                    println(File(photoPath.toString()).name)
                    val fileToUpload: MultipartBody.Part = MultipartBody.Part.createFormData("image", File(photoPath.toString()).name + ".jpg", requestBody)
                    //get photo url from server
                    viewModel.getImageURL(fileToUpload, context, onSuccess = { imageUrl ->
                        photoUrls = photoUrls + imageUrl
                        println("getting image urls in for iteration")
                    }
                    )
                }

                println("for iteration done")

                //upload post
                if(photoPaths.isNotEmpty() && photoUrls.isEmpty()) {
                    showToast(context, "photo Url이 없어 업로드에 실패했습니다.")
                }
                else {
                    var photos = photoUrls.map { PhotoReqDTO(it) }
                    val postData = UploadPostRequest(restaurant = restaurant, photos = photos, rating = rating, description = description)
                    viewModel.uploadPost(postData, context, onSuccess = onClick)
                }
            }
        }
    }

    MainButton(onClickReal, "리뷰 작성")
}
