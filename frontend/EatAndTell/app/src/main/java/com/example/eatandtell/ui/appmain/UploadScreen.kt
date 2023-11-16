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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.eatandtell.dto.PhotoReqDTO
import com.example.eatandtell.dto.RestReqDTO
import com.example.eatandtell.dto.SearchedRestDTO
import com.example.eatandtell.dto.UploadPostRequest
import com.example.eatandtell.dto.UserDTO
import com.example.eatandtell.ui.DraggableStarRating
import com.example.eatandtell.ui.ImageDialog
import com.example.eatandtell.ui.MainButton
import com.example.eatandtell.ui.MediumWhiteButton
import com.example.eatandtell.ui.PostImage
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.WhiteTextField
import com.example.eatandtell.ui.showToast
import com.example.eatandtell.ui.theme.Black
import com.example.eatandtell.ui.theme.MainColor
import kotlinx.coroutines.CancellationException
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
fun UploadScreen(navController: NavHostController, context: ComponentActivity, viewModel: AppMainViewModel, searchId : Int?, placeName : String?, categoryName : String?) {

    var restaurantName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(placeName ?: ""))
    }

//    var reviewDescription by rememberSaveable(stateSaver = TextFieldValue.Saver) {
//        mutableStateOf(TextFieldValue(""))
//    }

    val reviewDescription by viewModel.reviewDescription
    var myRating by rememberSaveable { mutableStateOf("0") }


// Directly use the ViewModel's state
    val photoPaths = viewModel.photoUris

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            // Update the ViewModel state when new images are selected
            viewModel.photoUris.clear()
            viewModel.photoUris.addAll(uris)
        }
    var clickedImageIndex by remember { mutableStateOf(-1) }



    var loading by remember { mutableStateOf(false) }
    var myProfile = viewModel.myProfile

    // Handle navigation result from SearchRestScreen
    LaunchedEffect(key1 = navController.currentBackStackEntryAsState()) {
        navController.currentBackStackEntry?.arguments?.getString("place_name")?.let {
            restaurantName = TextFieldValue(it)
        }
    }
//    LaunchedEffect(loading) {
//        try {
//            viewModel.getMyProfile (
//                onSuccess = { it ->
//                    myProfile = it
//                    println("myProfile: ${myProfile.username}")
//                }
//            )
//            loading = false
//        }
//        catch (e: Exception) {
//            if (e !is CancellationException) { // 유저가 너무 빨리 화면을 옮겨다니는 경우에는 CancellationException이 발생할 수 있지만, 서버 에러가 아니라서 패스
//                loading = false
//                println("get my profile load error")
//                showToast(context, "프로필 로딩에 실패하였습니다")
//            }
//        }
//    }

    if(loading) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                //로딩 화면
                modifier = Modifier
                    .size(70.dp),
                color = MainColor
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

                // Restaurant Name or Button
                if (restaurantName.text.isEmpty()) {
                    MediumWhiteButton(
                        onClick = { navController.navigate("SearchRest") },
                        text = "식당 검색"
                    )
                } else {
                    Text(text = restaurantName.text, style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight(700),
                        color = Black
                    ), modifier = Modifier.weight(1f) .height(22.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
//                    WhiteTextField(
//                        value = restaurantName.text,
//                        onValueChange = { newText ->
//                            restaurantName = TextFieldValue(newText)
//                        },
//                        placeholder = "식당 이름",
//                        textStyle = TextStyle(
//                            fontSize = 16.sp,
//                            lineHeight = 21.sp,
//                            fontWeight = FontWeight(700),
//                            color = Black
//                        ),
//                        modifier = Modifier
//                            .weight(1f)
//                            .border(
//                                width = 0.5.dp,
//                                color = Color(0xFFC5C5C5),
//                                shape = RoundedCornerShape(4.dp)
//                            )
//                    )
                    IconButton(
                        onClick = { navController.navigate("SearchRest") },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "식당 선택"
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                DraggableStarRating(
                    currentRating = myRating.toInt(),
                    onRatingChanged = { myRating = it.toString() })
            }

            // Review Text Field
            Spacer(modifier = Modifier.height(12.dp))
            WhiteTextField(
                value = reviewDescription,
                onValueChange = { viewModel.reviewDescription.value = it },
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
                restaurant = RestReqDTO(name = restaurantName.text, search_id = searchId, category_name = categoryName),
                photoPaths = photoPaths,
                rating = myRating,
                description = reviewDescription,
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
    var notLoading by remember { mutableStateOf(true) }


    val onClickReal: () -> Unit = {
        when {
            restaurant.name.isBlank() -> showToast(context, "맛집명을 입력해주세요")
            description.isBlank() -> showToast(context, "리뷰를 입력해주세요")
            rating == "0" -> showToast(context, "별점을 입력해주세요")

            else -> {
                try {
                    coroutineScope.launch {
                        notLoading = false
                        viewModel.uploadPhotosAndPost(
                            photoPaths = viewModel.photoUris, // Use ViewModel's photoUris
                            restaurant = restaurant,
                            rating = rating,
                            description = description,
                            context = context
                        )
                        onClickNav() //Navigation을 먼저 해버리니까
                    }
                } catch (e: Exception) {
                    // Handle exceptions, e.g., from network calls, here
                    showToast(context, "An error occurred: ${e.message}")
                }
            }

        }
    }

    MainButton(onClickReal, "리뷰 작성", notLoading = notLoading)
}
