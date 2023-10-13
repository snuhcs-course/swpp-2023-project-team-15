// SignUpScreen.kt
package com.example.eatandtell.ui.appmain
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.eatandtell.dto.PhotoReqDTO
import com.example.eatandtell.dto.RestReqDTO
import com.example.eatandtell.dto.UploadPostRequest
import com.example.eatandtell.ui.MainButton
import com.example.eatandtell.ui.MediumWhiteButton
import com.example.eatandtell.ui.PostImage
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.StarRating
import com.example.eatandtell.ui.WhiteTextField
import com.example.eatandtell.ui.showToast

@Composable
fun UploadScreen(navController: NavController, context: ComponentActivity, viewModel: AppMainViewModel) {

    var restaurantName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var reviewDescription by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }


    val profileUrl = "https://newprofilepic.photo-cdn.net//assets/images/article/profile.jpg?90af0c8"
    val photoUrls = listOf(
        "https://api.nudge-community.com/attachments/339560",
        "https://img.siksinhot.com/place/1650516612762055.jpg?w=560&h=448&c=Y",
        "https://img1.daumcdn.net/thumb/R800x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FdKS0uX%2FbtrScbvc9HH%2F5I2m53vgz0LWvszHQ9PQNk%2Fimg.jpg"
    )
    val username = "Joshua-i"
    val userDescription = "고독한 미식가"

    // Main content of LoginActivity
    Column(
        modifier = Modifier
            .fillMaxSize().padding(horizontal = 20.dp)
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        // Profile Row
//        Spacer(modifier = Modifier.height(10.dp))
        Profile(profileUrl, username, userDescription);

        Spacer(modifier = Modifier.height(16.dp))

        // Images Row
        Row(
            modifier = Modifier
                .height(150.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            for (photoUrl in photoUrls) {
                PostImage(photoUrl)
            }
        }

        // Medium White Button
        Spacer(modifier = Modifier.height(16.dp))
        MediumWhiteButton(onClick = { /*TODO*/ }, text = "사진 추가하기")

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
            StarRating(rating = "0", size = 24.dp)
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
            photos = photoUrls.map { PhotoReqDTO(it) } ,
            rating = "4",
            description = reviewDescription.text,
            context = context,
            onClick = {
                navController.navigate("home")
            }
        )

        Spacer(modifier = Modifier.height(20.dp))
    }

}

//@Preview(showBackground = true)
//@Composable
//fun UploadScreenPreview() {
//    UploadScreen(UploadActivity())
//}

@Composable
fun UploadButton(viewModel: AppMainViewModel,
                 restaurant : RestReqDTO,
                 photos: List<PhotoReqDTO>,
                 rating: String,
                 description: String,
                 context: Context,
                 onClick: () -> Unit) {
    val postData = UploadPostRequest(restaurant = restaurant, photos = photos, rating = rating, description = description)
    val onClickReal = {
        viewModel.uploadPost(postData, object: AppMainViewModel.UploadCallback{
            override fun onUploadSuccess() {
                onClick()
            }
            override fun onUploadError(errorMessage: String) {
                showToast(context, errorMessage)
            } } )}
    MainButton(onClick, "리뷰 작성") //TODO: 이후 onClickReal로 변경해야 백엔드와 연결됨
}
