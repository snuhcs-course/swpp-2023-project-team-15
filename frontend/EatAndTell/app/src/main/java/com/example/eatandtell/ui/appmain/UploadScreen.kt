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
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.eatandtell.dto.PhotoReqDTO
import com.example.eatandtell.dto.RestReqDTO
import com.example.eatandtell.dto.UploadPostRequest
import com.example.eatandtell.ui.DraggableStarRating
import com.example.eatandtell.ui.ImageDialog
import com.example.eatandtell.ui.MainButton
import com.example.eatandtell.ui.MediumWhiteButton
import com.example.eatandtell.ui.PostImage
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.WhiteTextField
import com.example.eatandtell.ui.showToast
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
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

    var photoPaths by remember { mutableStateOf(listOf<Uri>()) } //핸드폰 내의 파일 경로

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            photoPaths = it
        }

    var clickedImageIndex by remember { mutableStateOf(-1) }



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

            for ((index, photoPath) in photoPaths.withIndex()) {
                PostImage(photoPath.toString(), onImageClick = { clickedImageIndex = index }
                )
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
                .weight(1f) // 남은 세로 길이 모두 리뷰 공간으로 할당,
            ,
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

    //If Image Clicked, show Image Dialog
    if (clickedImageIndex != -1) {
        ImageDialog(imageUrl = photoPaths[clickedImageIndex].toString(), onClick = { clickedImageIndex = -1 })
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
    val coroutineScope = rememberCoroutineScope()

    val onClickReal: () -> Unit = {
        when {
            restaurant.name.isBlank() -> showToast(context, "맛집명을 입력해주세요")
            description.isBlank() -> showToast(context, "리뷰를 입력해주세요")
            rating == "0" -> showToast(context, "별점을 입력해주세요")

            else -> {
                try {
                    coroutineScope.launch {
                        viewModel.uploadPhotosAndPost(
                            photoPaths = photoPaths,
                            restaurant = restaurant,
                            rating = rating,
                            description = description,
                            context = context
                        )
                        onClick()
                    }
                } catch (e: Exception) {
                    // Handle exceptions, e.g., from network calls, here
                    showToast(context, "An error occurred: ${e.message}")
                }
            }

        }
    }

    MainButton(onClickReal, "리뷰 작성")
}
