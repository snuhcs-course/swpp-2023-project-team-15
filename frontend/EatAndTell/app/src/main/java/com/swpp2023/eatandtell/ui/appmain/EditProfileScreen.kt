package com.swpp2023.eatandtell.ui.appmain
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import com.swpp2023.eatandtell.data.security.SharedPreferencesManager
import com.swpp2023.eatandtell.ui.CustomTextField
import com.swpp2023.eatandtell.ui.EditProfileImage
import com.swpp2023.eatandtell.ui.MediumRedButton
import com.swpp2023.eatandtell.ui.start.StartActivity
import com.swpp2023.eatandtell.ui.theme.Black
import com.swpp2023.eatandtell.ui.theme.MainColor
import kotlinx.coroutines.launch
@Composable
fun EditProfileScreen(context: ComponentActivity, viewModel: AppMainViewModel, navController: NavHostController) {

    // Observe changes in ViewModel
    val loading =false
    //by viewModel.editProfileLoading.collectAsState()
    fun hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(context.currentFocus?.windowToken, 0)
    }

    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { hideKeyboard() }
                )
            }
            .fillMaxSize()
            .padding(16.dp)
            .testTag("edit_profile"),
    ) {

        val coroutineScope = rememberCoroutineScope()


//        var loading by remember { mutableStateOf(false) }
        var myProfile = viewModel.myProfile

        var buttonEnable by remember { mutableStateOf(true) }


        // Observe loading state
        if (loading) {
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

            var photoPaths by remember { mutableStateOf(listOf<Uri>()) } //핸드폰 내의 파일 경로

            val galleryLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
                    photoPaths = it
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
                Spacer(modifier = Modifier.height(25.dp))

//                NameText(text = "아이디")
//                CustomTextField( // username은 바꾸지 않고 보여주기만 한다.
//                    value = myProfile.username,
//                    onValueChange = { },
//                    placeholder = "아이디를 입력하세요 (4~20자)",
//                    enable = false,
//                )
                Spacer(modifier = Modifier.height(25.dp))

                NameText(text = "자기소개")
                Spacer(modifier = Modifier.height(25.dp))
                CustomTextField(
                    value = description.text,
                    onValueChange = { description = TextFieldValue(it) },
                    placeholder = "자기소개를 입력하세요",
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(25.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MediumRedButton(onClick = {
                        SharedPreferencesManager.clearPreferences(context)
                        //go to StartActivity's Login Screen
                        val intent = Intent(context, StartActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(context, intent, null)
                    }, text = "로그아웃")

                    MediumRedButton(onClick = {
                                try {
                                    buttonEnable = false
                                    coroutineScope.launch {
                                        viewModel.uploadPhotosAndEditProfile(
                                            photoPaths = photoPaths,
                                            description = description.text,
                                            context = context,
                                            org_avatar_url = myProfile.avatar_url,
                                        )
                                        navigateToDestination(navController, "Profile")
                                    }
                                } catch (e: Exception) {
                                    // Handle exceptions, e.g., from network calls, here
                                    println("An error occurred: ${e.message}")
                                }
                            }, text = "프로필 저장", enable = buttonEnable)

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NameText(text: String = "아이디") {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = TextStyle(
            fontSize = 16.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight(500),
            color = Black,
        ), modifier = Modifier
            .height(22.dp),
        )
    }
}



