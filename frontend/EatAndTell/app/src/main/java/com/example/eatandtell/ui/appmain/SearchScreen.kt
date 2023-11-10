package com.example.eatandtell.ui.appmain

import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.eatandtell.dto.PostDTO
import com.example.eatandtell.dto.TopTag
import com.example.eatandtell.dto.UserDTO
import com.example.eatandtell.ui.CustomTextField
import com.example.eatandtell.ui.MediumWhiteButton
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.SearchSelectButton
import com.example.eatandtell.ui.Tag
import com.example.eatandtell.ui.showToast
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(navController: NavHostController, context: ComponentActivity, viewModel: AppMainViewModel) {
    var searchText by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var userLists by remember { mutableStateOf(emptyList<UserDTO>()) }

    var userListsByTags by remember { mutableStateOf(emptyList<UserDTO>()) }

//    var postLists by remember { mutableStateOf(emptyList<PostDTO>()) }
    val postLists = remember { mutableStateListOf<PostDTO>() }

    var loading by remember { mutableStateOf(false) }

    var triggerSearch by remember { mutableStateOf(false) }

    var topTags by remember { mutableStateOf<List<TopTag>>(emptyList()) }

    var selectedButton: String by remember { mutableStateOf("유저") }

    val debouncePeriod = 300L

    val searchJob = remember { mutableStateOf<Job?>(null) }


    //searchBar
    Column (
        modifier = Modifier
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
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SearchSelectButton(
                onClick = {
                    selectedButton = "유저"
                    triggerSearch = true
                },
                text = "유저",
                selected = selectedButton != "유저"
            )

            Spacer(modifier = Modifier.width(16.dp))

            SearchSelectButton(
                onClick = {
                    selectedButton = "태그"
                    triggerSearch = true
                },
                text = "태그",
                selected = selectedButton != "태그"
            )

            Spacer(modifier = Modifier.width(16.dp))

            SearchSelectButton(
                onClick = {
                    selectedButton = "식당"
                    triggerSearch = true
                },
                text = "식당",
                selected = selectedButton != "식당"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        LaunchedEffect(Unit) { // LaunchedEffect with Unit to run only once
            viewModel.getTopTags(
                onSuccess = { tags ->
                    topTags = tags.take(5) // Take the first five tags
                },
                onError = { errorMessage ->
                    showToast(context, "Failed to load top tags: $errorMessage")
                }
            )
        }

        // show defaultTags when selectedbutton is "tag" and
        if(selectedButton == "태그" && userListsByTags.isEmpty()) {
            // Check if both lists are empty and triggerSearch is false
            DefaultTagView(searchText.text, topTags.map { it.ko_label }) { tag ->
                // Set the searchText to the tag that was clicked
                searchText = TextFieldValue("$tag")
                // Trigger the search
                triggerSearch = true
            }
        }
        //search for userLists
        LaunchedEffect(triggerSearch) {
            println("search screen "+searchText.text + " " + triggerSearch)
            searchJob.value?.cancel() // 이전 검색 작업이 있다면 취소
            searchJob.value = launch {
                delay(debouncePeriod)
                if (searchText.text.isNotEmpty()) {
                    postLists.clear() // This will clear the MutableStateList
                    userLists = emptyList();
                    userListsByTags = emptyList();
                    loading = true
                    try {
                        if (selectedButton == "유저") { // If search by user@
                            if (searchText.text.length >= 1) viewModel.getFilteredUsersByName( // 실질 searchtext가 존재하는 경우만 검색
                                searchText.text, // Remove @ from the search string
                                onSuccess = { users ->
                                    userLists = users // resulted user Lists
                                    loading = false
                                }
                            )
                            postLists.clear() // This will clear the MutableStateList
                        } else if (selectedButton == "태그") {
                            //TODO: search by tags
                            if (searchText.text.length >= 1) viewModel.getFilteredUsersByTag(
                                searchText.text,
                                onSuccess = { users ->
                                    userListsByTags = users // resulted user Lists
                                    loading = false
                                }
                            )
                            postLists.clear() // This will clear the MutableStateList
                        } else {
                            if (searchText.text.length >= 1) viewModel.getFilteredByRestaurants(
                                searchText.text,
                                onSuccess = { posts ->
                                    postLists.clear() // This will clear the MutableStateList
                                    postLists.addAll(posts)
                                    loading = false
                                }
                            )
                            userLists = emptyList() // Reset user lists
                            userListsByTags = emptyList()
                        }
                    } catch (e: Exception) {
                        println("searchload error")
                        showToast(context, "search 로딩에 실패하였습니다")
                        loading = false
                    }
                    triggerSearch = false // reset the trigger
                }
            }
            triggerSearch = false
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
                            onLike = { postToLike ->
                                val index = postLists.indexOf(postToLike)
                                if (index != -1) {
                                    val newLikeCount = if (postToLike.is_liked) postToLike.like_count - 1 else postToLike.like_count + 1
                                    postLists[index] = postToLike.copy(
                                        is_liked = !postToLike.is_liked,
                                        like_count = newLikeCount
                                    )
                                }
                            },
                            onDelete = { postToDelete ->
                                postLists.remove(postToDelete)
                            }
                        )
                    }
                }

                // navigation bottom app bar 때문에 스크롤이 가려지는 것 방지 + 20.dp padding
                item { Spacer(modifier = Modifier.height(70.dp)) }
            }

        }
    }

}

@Composable
fun SearchBar(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit, onSearchClick: () -> Unit) {
    CustomTextField(
        value = value.text,
        onValueChange = { onValueChange(TextFieldValue(it)) },
        placeholder = "",
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
        }
    )
}

@Composable
fun DefaultTagView(text: String, tags: List<String>, onTagClick: (String) -> Unit = {}) {
    if (
        text == "@" ||
        text == "#" ||
        text.isEmpty()
    ) {
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
}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen(navController = rememberNavController(), context = ComponentActivity(), viewModel = AppMainViewModel())
}




