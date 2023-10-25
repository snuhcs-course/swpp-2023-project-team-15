package com.example.eatandtell.ui.appmain
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.example.eatandtell.SharedPreferencesManager
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.RestaurantDTO
import com.example.eatandtell.dto.UserDTO
import com.example.eatandtell.dto.UserInfoDTO
import com.example.eatandtell.ui.CustomTextField
import com.example.eatandtell.ui.EditProfileImage
import com.example.eatandtell.ui.HeartEmpty
import com.example.eatandtell.ui.HeartFull
import com.example.eatandtell.ui.Logo
import com.example.eatandtell.ui.MediumRedButton
import com.example.eatandtell.ui.Post
import com.example.eatandtell.ui.PostImage
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.ProfileImage
import com.example.eatandtell.ui.StarRating
import com.example.eatandtell.ui.Tag
import com.example.eatandtell.ui.showToast
import com.example.eatandtell.ui.start.PasswordVisibilityToggle
import com.example.eatandtell.ui.start.StartActivity
import com.example.eatandtell.ui.theme.Black
import com.example.eatandtell.ui.theme.Inter
import com.example.eatandtell.ui.theme.MainColor

import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
@Composable
fun EditProfileScreen(context: ComponentActivity, viewModel: AppMainViewModel, navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {

        val coroutineScope = rememberCoroutineScope()

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
                    showToast(context, "프로필 로딩에 실패하였습니다")
                }
            }
        }

        if (loading) {
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

            var photoPaths by remember { mutableStateOf(listOf<Uri>()) } //핸드폰 내의 파일 경로


            val galleryLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
                    photoPaths = it
                }

            var username by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                mutableStateOf(TextFieldValue(myProfile.username))
            }

            var description by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                mutableStateOf(TextFieldValue(myProfile.description))
            }

            // Main content of SignupActivity
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {

                EditProfileImage(
                    profileUrl = if (photoPaths.isEmpty()) myProfile.avatar_url else photoPaths[0].toString(),
                    onEditClick = { galleryLauncher.launch("image/*") }
                    , size = 100.dp)
                Spacer(modifier = Modifier.height(17.dp))

                CustomTextField(
                    value = username.text,
                    onValueChange = { username = TextFieldValue(it) },
                    placeholder = "아이디를 입력하세요 (4~20자)",
                )
                Spacer(modifier = Modifier.height(12.dp))

                CustomTextField(
                    value = description.text,
                    onValueChange = { description = TextFieldValue(it) },
                    placeholder = "자기소개를 입력하세요",
                    maxLines = 8,
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MediumRedButton(onClick = {
                        when {
                            username.text.isBlank() -> showToast(context, "아이디를 입력하세요")
                            (username.text.length) !in 4..20 -> showToast(context, "아이디가 올바르지 않습니다")

                            else -> {
                                try {
                                    coroutineScope.launch {
                                        //TODO: 백엔드에서 edit profile 구현되면 확인하기
//                                viewModel.uploadPhotosAndEditProfile(
//                                    photoPaths = photoPaths,
//                                    username = username.text,
//                                    description = description.text,
//                                    context = context
//                                )
                                        navigateToDestination(navController, "Profile")
                                    }
                                } catch (e: Exception) {
                                    // Handle exceptions, e.g., from network calls, here
                                    showToast(context, "An error occurred: ${e.message}")
                                }
                            }

                        }
                    }, text = "프로필 저장")

                    MediumRedButton(onClick = {
                        SharedPreferencesManager.clearPreferences(context)
                        //go to StartActivity's Login Screen
                        val intent = Intent(context, StartActivity::class.java)
                        startActivity(context, intent, null)
                    }, text = "로그아웃")

                }
            }
        }
    }
}



