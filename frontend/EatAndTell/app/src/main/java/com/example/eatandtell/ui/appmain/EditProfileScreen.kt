package com.example.eatandtell.ui.appmain
import android.content.Intent
import androidx.activity.ComponentActivity
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.eatandtell.SharedPreferencesManager
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.RestaurantDTO
import com.example.eatandtell.dto.UserDTO
import com.example.eatandtell.dto.UserInfoDTO
import com.example.eatandtell.ui.HeartEmpty
import com.example.eatandtell.ui.HeartFull
import com.example.eatandtell.ui.MediumRedButton
import com.example.eatandtell.ui.Post
import com.example.eatandtell.ui.PostImage
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.StarRating
import com.example.eatandtell.ui.Tag
import com.example.eatandtell.ui.showToast
import com.example.eatandtell.ui.start.StartActivity
import com.example.eatandtell.ui.theme.Black
import com.example.eatandtell.ui.theme.Inter
import com.example.eatandtell.ui.theme.MainColor

import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch
@Composable
fun EditProfileScreen(context: ComponentActivity, viewModel: AppMainViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        MediumRedButton(onClick = {
            SharedPreferencesManager.clearPreferences(context)
            //go to StartActivity's Login Screen
            val intent = Intent(context, StartActivity::class.java)
            startActivity(context, intent, null)
        }, text = "로그아웃")
    }
}



