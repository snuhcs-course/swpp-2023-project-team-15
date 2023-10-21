package com.example.eatandtell.ui.appmain

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.eatandtell.dto.UserDTO
import com.example.eatandtell.ui.CustomTextField
import com.example.eatandtell.ui.Profile
import com.example.eatandtell.ui.Tag
import com.example.eatandtell.ui.showToast
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun SearchScreen(navController: NavHostController, context: ComponentActivity, viewModel: AppMainViewModel) {
    var searchText by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var userLists by remember { mutableStateOf(emptyList<UserDTO>()) }

    var loading by remember { mutableStateOf(false) }

    var triggerSearch by remember { mutableStateOf(false) }


    //        SearchBar(searchText = searchText, onValueChange = { searchText = TextFieldValue(it)})
    //searchBar
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),)
    {
        Spacer(modifier = Modifier.height(11.dp))
        CustomTextField(
            value = searchText.text,
            onValueChange = { searchText = TextFieldValue(it)},
            placeholder = "Search by id, restaurant, user tags",
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            onTrailingIconClick = {
                triggerSearch = true
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        val tags = listOf("#육식주의자", "#미식가", "#리뷰왕", "#감성", "#한식")
        //Tags
        FlowRow(
            modifier = Modifier
                .fillMaxWidth(),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            tags.forEach { tagName ->
                Tag(tagName)
            }
        }
        Spacer(modifier = Modifier.height(11.dp))

        //search for userLists
        LaunchedEffect(triggerSearch) {
            if (triggerSearch) {
                loading = true
                try {
                    viewModel.getFilteredUsers(
                        searchText.text,
                        onSuccess = { users ->
                            userLists = users // resulted user Lists
                            loading = false
                        },
                    )
                }
                catch (e: Exception) {
                    println("searchload error")
                    showToast(context, "search 로딩에 실패하였습니다")
                    loading = false
                }
                triggerSearch = false // reset the trigger
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
        else{
            //for users
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(userLists.size) { index ->
                    val user = userLists[index]
                    Profile(
                        profileUrl = user.avatar_url,
                        username = user.username,
                        userDescription = user.description,
                        onImageClick = {
                            navController.navigate("Profile/${user.id}")
                        },
                        onDescriptionClick = {
                            navController.navigate("Profile/${user.id}")
                        },
                        onUsernameClick = {
                            navController.navigate("Profile/${user.id}")
                        }
                    )
                }
            }
        }
    }

}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen(navController = rememberNavController(), context = ComponentActivity(), viewModel = AppMainViewModel())
}
//
//@Composable
//fun SearchBar(searchText: TextFieldValue, onValueChange: (String) -> Unit){
//    Column (
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(horizontal = 20.dp),)
//    {
//        Spacer(modifier = Modifier.height(11.dp))
//        //SearchBar
//        CustomTextField(
//            value = searchText.text,
//            onValueChange = onValueChange,
//            placeholder = "Search by id, restaurant, user tags",
//            trailingIcon = {
//                Icon(
//                    imageVector = Icons.Default.Search,
//                    contentDescription = "Search Icon"
//                )
//            },
//            onTrailingIconClick = {
//                // Define your action here. For instance:
////                viewModel.search(searchText.text) // assuming you have a search method in your viewModel
//            }
//        )
//        Spacer(modifier = Modifier.height(20.dp))
//        val tags = listOf("#육식주의자", "#미식가", "#리뷰왕", "#감성", "#한식")
//        //Tags
//        FlowRow(
//            modifier = Modifier
//                .fillMaxWidth(),
//            mainAxisSpacing = 8.dp,
//            crossAxisSpacing = 8.dp
//        ) {
//            tags.forEach { tagName ->
//                Tag(tagName)
//            }
//        }
//    }
//}



