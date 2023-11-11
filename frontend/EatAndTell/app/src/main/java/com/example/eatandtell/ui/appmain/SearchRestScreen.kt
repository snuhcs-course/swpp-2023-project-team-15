// SignUpScreen.kt
package com.example.eatandtell.ui.appmain

import android.Manifest
import android.R.attr.path
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.eatandtell.dto.GetSearchedRestResponse
import com.example.eatandtell.dto.PhotoReqDTO
import com.example.eatandtell.dto.RestReqDTO
import com.example.eatandtell.dto.SearchedRestDTO
import com.example.eatandtell.dto.UploadPostRequest
import com.example.eatandtell.dto.UserDTO
import com.example.eatandtell.ui.CustomTextField
import com.example.eatandtell.ui.DraggableStarRating
import com.example.eatandtell.ui.ImageDialog
import com.example.eatandtell.ui.MainButton
import com.example.eatandtell.ui.MediumWhiteButton
import com.example.eatandtell.ui.PostImage
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.WhiteTextField
import com.example.eatandtell.ui.showToast
import com.example.eatandtell.ui.theme.Black
import com.example.eatandtell.ui.theme.Gray
import com.example.eatandtell.ui.theme.Inter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
fun SearchRestScreen(navController: NavHostController, context: AppMainActivity, viewModel: AppMainViewModel) {

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    var searchQuery by remember { mutableStateOf("") }
    var selectedResult by remember { mutableStateOf<SearchedRestDTO?>(null) }

    var searchResults by remember { mutableStateOf(listOf<SearchedRestDTO>()) }
    var scrollState = rememberScrollState(0)

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            val res = viewModel.getSearchedRest(searchQuery, context.positionX, context.positionY)
            searchResults = res
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(modifier = Modifier.height(11.dp))

        // Search Bar
        CustomTextField(
            value = searchQuery,
            onValueChange = { newQuery ->
                searchQuery = newQuery
                Log.d("search rest", "new searchQuery: $searchQuery")
                selectedResult = null // Reset selected result when query changes
            },
            placeholder = "맛집을 검색하세요",
            trailingIcon = {
                Box(
                    modifier = Modifier.clickable(onClick = {
                            //아무것도 안해도 됨
                        }
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Search Results
        if (searchQuery == "") {
            Text(
                text = if(context.positionX=="") "검색어가 없습니다.\n위치 권한이 꺼져있어 관악구 중심으로 검색됩니다." else "검색어가 없습니다.\n현재 위치를 기반으로 검색됩니다.",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
                    .weight(1f)
                ,
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(500),
                    color = Color.Gray,
                ),
            )
        }

        else if (searchResults.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                searchResults.forEach { result ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = {
                                selectedResult =
                                    if (selectedResult == null || result.id != selectedResult!!.id) result
                                    else null
                                //searchQuery = result.place_name //TODO or not?
                            })
                            .background(
                                if (selectedResult == null || result.id != selectedResult!!.id) Color.White
                                else Color.LightGray
                            )
                    ) {
                        Divider()
                        Spacer (modifier = Modifier.height(10.dp))
                        Text(
                            text = result.place_name,
                            style = TextStyle(
                                fontFamily = Inter,
                                fontSize = 16.sp,
                                fontWeight = FontWeight(500),
                                color = Black,
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Text(
                            text = result.road_address_name,
                            style = TextStyle(
                                fontFamily = Inter,
                                fontSize = 14.sp,
                                fontWeight = FontWeight(400),
                                color = Gray,
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Spacer (modifier = Modifier.height(10.dp))
                    }
                }
            }
        }


        else {
            Text(
                text = "검색 결과가 없습니다",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
                    .weight(1f),
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(500),
                    color = Color.Gray,
                ),
            )
        }

        //submit button
        Spacer(modifier = Modifier.height(16.dp))
        MainButton(onClick = {
            navController.navigate("Upload/${selectedResult?.id}/${selectedResult?.place_name}/${selectedResult?.category_name}")
        }, text = "식당 선택", enabled = selectedResult != null)
        Spacer(modifier = Modifier.height(70.dp))


    }
}



