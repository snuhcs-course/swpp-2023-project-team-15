package com.swpp2023.eatandtell.ui.appmain

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.swpp2023.eatandtell.dto.PostDTO
import com.swpp2023.eatandtell.dto.TopTag
import com.swpp2023.eatandtell.dto.UserDTO
import com.swpp2023.eatandtell.ui.CustomTextField
import com.swpp2023.eatandtell.ui.Profile
import com.swpp2023.eatandtell.ui.SearchSelectButton
import com.swpp2023.eatandtell.ui.Tag
import com.swpp2023.eatandtell.ui.showToast
import com.swpp2023.eatandtell.ui.theme.Inter
import com.swpp2023.eatandtell.ui.theme.MainColor
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(navController: NavHostController, context: ComponentActivity, viewModel: AppMainViewModel) {
    var searchText by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }


    val coroutineScope = rememberCoroutineScope()


    var triggerSearch by remember { mutableStateOf(false) }


    var selectedButton: String by remember { mutableStateOf("유저") }

    val debouncePeriod = 300L

    val searchJob = remember { mutableStateOf<Job?>(null) }

    // Observing state changes

    val userLists by viewModel.userLists.collectAsState()
    val userListsByTags by viewModel.userListsByTags.collectAsState()
    val postLists by viewModel.postLists.collectAsState()
    val loading by viewModel.searchLoading.collectAsState()
    val topTags by viewModel.topTags.collectAsState()

    fun hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(context.currentFocus?.windowToken, 0)
    }

    //searchBar
    Column (
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { hideKeyboard() }
                )
            }
            .fillMaxSize()
            .padding(horizontal = 20.dp),)
    {
        Spacer(modifier = Modifier.height(11.dp))
        SearchBar(
            value = searchText,
            onValueChange = { searchText = it;  triggerSearch = true; },
            onSearchClick = { triggerSearch = true }
        )
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), // Row가 화면의 전체 가로 길이를 차지하도록 설정
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SearchSelectButton(
                onClick = {
                    selectedButton = "유저"
                    triggerSearch = true
                    searchText = TextFieldValue("")
                },
                text = "유저",
                selected = selectedButton != "유저"
            )


            SearchSelectButton(
                onClick = {
                    selectedButton = "태그"
                    triggerSearch = true
                    searchText = TextFieldValue("")
                },
                text = "태그",
                selected = selectedButton != "태그"
            )


            SearchSelectButton(
                onClick = {
                    selectedButton = "식당"
                    triggerSearch = true
                    searchText = TextFieldValue("")
                },
                text = "식당",
                selected = selectedButton != "식당"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        LaunchedEffect(Unit) {
            viewModel.fetchTopTags()
        }

        // Trigger search logic
        LaunchedEffect(searchText.text, selectedButton) {
                searchJob.value?.cancel() // Cancel previous job
                searchJob.value = coroutineScope.launch {
                        delay(debouncePeriod)
                        viewModel.performSearch(searchText.text, selectedButton)
                        triggerSearch = false
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
                        .size(70.dp),
                    color= MainColor
                )
            }
        }
        else {
            if (searchText.text == "") {
                // show defaultTags when selectedbutton is "tag" and
                if(selectedButton == "태그" && userListsByTags.isEmpty()) {
                    // Check if both lists are empty and triggerSearch is false
                    DefaultTagView(topTags.map { it.ko_label }) { tag ->
                        // Set the searchText to the tag that was clicked. set text cursor point to last
                        val newText = "$tag"
                        searchText = TextFieldValue(newText, TextRange(newText.length))
                        // Trigger the search
                        triggerSearch = true
                    }
                }
                Text(
                    text = "검색어가 없습니다.",
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
            else if(userLists.isNotEmpty() || userListsByTags.isNotEmpty() || postLists.isNotEmpty()){
                LazyColumn(
                    state = rememberLazyListState(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (selectedButton == "유저") {
                        items(userLists.size) { index ->
                            val user = userLists[index]
                            Profile(
                                profileUrl = user.avatar_url,
                                username = user.username,
                                userDescription = user.description,
                                onClick = {
                                    if (user.id == viewModel.myProfile?.id) {
                                        navController.navigate("Profile")
                                    } else {
                                        navController.navigate("Profile/${user.id}")
                                    }
                                },
                            )
                        }
                    }
                    else if (selectedButton == "태그"){
                        items(userListsByTags.size) { index ->
                            val user = userListsByTags[index]
                            Profile(
                                profileUrl = user.avatar_url,
                                username = user.username,
                                userDescription = user.description,
                                onClick = {
                                    if (user.id == viewModel.myProfile?.id) {
                                        navController.navigate("Profile")
                                    } else {
                                        navController.navigate("Profile/${user.id}")
                                    }
                                },
                            )
                        }
                    } else {
                        items(items = postLists, key = { it.id }) { post ->
                            HomePost(
                                post = post,
                                viewModel = viewModel,
                                navHostController = navController,
                            )
                        }
                    }
                    // navigation bottom app bar 때문에 스크롤이 가려지는 것 방지 + 20.dp padding
                    item { Spacer(modifier = Modifier.height(70.dp)) }
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

        }
    }

}

@Composable
fun SearchBar(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit, onSearchClick: () -> Unit) {
    CustomTextField(
        value = value.text,
        onValueChange = { onValueChange(TextFieldValue(it)) },
        placeholder = "유저, 태그, 식당 중에서 검색해보세요",
        trailingIcon = {
            Box(
                modifier = Modifier.clickable(onClick = onSearchClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            }
        },
        testTag = "search_bar_text_field",
    )
}



@Composable
fun DefaultTagView(tags: List<String>, onTagClick: (String) -> Unit = {}) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            tags.forEach { tagName ->
                Tag(tagName) {
                    onTagClick(tagName)
                }
            }
        }
        Spacer(modifier = Modifier.height(11.dp))

}





