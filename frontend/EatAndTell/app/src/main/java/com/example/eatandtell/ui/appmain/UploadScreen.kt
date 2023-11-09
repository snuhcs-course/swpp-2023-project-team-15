// SignUpScreen.kt
package com.example.eatandtell.ui.appmain

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eatandtell.dto.RestReqDTO
import com.example.eatandtell.dto.UserDTO
import com.example.eatandtell.ui.DraggableStarRating
import com.example.eatandtell.ui.ImageDialog
import com.example.eatandtell.ui.MainButton
import com.example.eatandtell.ui.MediumWhiteButton
import com.example.eatandtell.ui.PostImage
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.WhiteTextField
import com.example.eatandtell.ui.showToast
import kotlinx.coroutines.CancellationException


@Composable
fun UploadScreen(navController: NavHostController, context: ComponentActivity, viewModel: AppMainViewModel) {

    var restaurantName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var reviewDescription by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var myRating by rememberSaveable { mutableStateOf("0") }

    var photoPaths by remember { mutableStateOf(listOf<Uri>()) } //핸드폰 내의 파일 경로

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            photoPaths = it
        }

    var clickedImageIndex by remember { mutableStateOf(-1) }

    val context = LocalContext.current
    val uploadMessage by viewModel.messageToDisplay.observeAsState()

    var loading by remember { mutableStateOf(true) }
    var myProfile by remember { mutableStateOf(UserDTO(0, "", "", "", listOf())) }

    LaunchedEffect(loading) {
        try {
            viewModel.getMyProfile (
                onSuccess = { it ->
                    myProfile = it
                    println("myProfile: ${myProfile.username}")
                }
            )
            loading = false
        }
        catch (e: Exception) {
            if (e !is CancellationException) { // 유저가 너무 빨리 화면을 옮겨다니는 경우에는 CancellationException이 발생할 수 있지만, 서버 에러가 아니라서 패스
                loading = false
                println("get my profile load error")
                println(e)
                showToast(context, "프로필 로딩에 실패하였습니다")
            }
        }
    }
    LaunchedEffect(key1 = uploadMessage){
        uploadMessage?.let{
            showToast(context,it)
        }
    }

    if(loading) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                //로딩 화면
                modifier = Modifier
                    .size(70.dp)
            )
        }
    }
    else {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // Profile Row
            Profile(myProfile.avatar_url, myProfile.username, myProfile.description);

            Spacer(modifier = Modifier.height(16.dp))

            // Images Row
            Row(
                modifier = Modifier
                    .height(150.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                //if photoPath is empty, show default image
                if (photoPaths.isEmpty()) {
                    PostImage(
                        onImageClick = { galleryLauncher.launch("image/*") } //can add photos
                    )
                }

                for ((index, photoPath) in photoPaths.withIndex()) {
                    PostImage(photoPath.toString(), onImageClick = { clickedImageIndex = index }
                    )
                }
            }

            // Medium White Button
            Spacer(modifier = Modifier.height(16.dp))
            MediumWhiteButton(onClick = { galleryLauncher.launch("image/*") }, text = "사진 선택하기")

            // Restaurant Name Text Field and Rating
            Spacer(modifier = Modifier.height(16.dp))

            Row(
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
                DraggableStarRating(
                    currentRating = myRating.toInt(),
                    onRatingChanged = { myRating = it.toString() })
            }

            // Review Text Field
            Spacer(modifier = Modifier.height(12.dp))
            WhiteTextField(
                value = reviewDescription.text,
                onValueChange = { reviewDescription = TextFieldValue(it) },
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
                photoPaths = photoPaths,
                rating = myRating,
                description = reviewDescription.text,
                context = context,
                onClickNav = {
                    navigateToDestination(navController, "Home")
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        //If Image Clicked, show Image Dialog
        if (clickedImageIndex != -1) {
            ImageDialog(
                imageUrl = photoPaths[clickedImageIndex].toString(),
                onClick = { clickedImageIndex = -1 })
        }
    }

}



@Composable
fun UploadButton(viewModel: AppMainViewModel,
                 restaurant : RestReqDTO,
                 photoPaths: List<Uri>,
                 rating: String,
                 description: String,
                 context: Context,
                 onClickNav: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var enable by remember { mutableStateOf(true) }


    val onClickReal: () -> Unit = {
        when {
            restaurant.name.isBlank() -> showToast(context, "맛집명을 입력해주세요")
            description.isBlank() -> showToast(context, "리뷰를 입력해주세요")
            rating == "0" -> showToast(context, "별점을 입력해주세요")

            else -> {
                try {
                    viewModel.uploadPhotosAndPost(
                        photoPaths = photoPaths,
                        restaurant = restaurant,
                        rating = rating,
                        description = description,
                        context = context
                    )
                    onClickNav()
                    /*coroutineScope.launch {
                        enable = false
                         //Navigation을 먼저 해버리니까
                    }*/
                } catch (e: Exception) {
                    // Handle exceptions, e.g., from network calls, here
                    showToast(context, "An error occurred: ${e.message}")
                }
            }

        }
    }

    MainButton(onClickReal, "리뷰 작성", enable = enable)
}
