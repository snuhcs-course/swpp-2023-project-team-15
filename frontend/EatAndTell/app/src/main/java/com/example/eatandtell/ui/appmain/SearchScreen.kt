package com.example.eatandtell.ui.appmain

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.eatandtell.ui.CustomTextField
import com.example.eatandtell.ui.Tag
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun SearchScreen(navController: NavHostController, context: ComponentActivity, viewModel: AppMainViewModel) {
    var searchText by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    Column (
        modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),)
    {
        Spacer(modifier = Modifier.height(11.dp))
        //SearchBar
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
                // Define your action here. For instance:
//                viewModel.search(searchText.text) // assuming you have a search method in your viewModel
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
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen(navController = rememberNavController(), context = ComponentActivity(), viewModel = AppMainViewModel())
}



