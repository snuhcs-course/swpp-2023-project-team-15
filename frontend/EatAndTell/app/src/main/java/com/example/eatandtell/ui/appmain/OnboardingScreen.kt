package com.example.eatandtell.ui.appmain

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.eatandtell.dto.OnboardingPage



class OnBoardingViewModel: ViewModel(){
    var currentPage: Int by mutableStateOf(0)

    fun setPage(page:Int){
        currentPage= page
    }
}

val onboardingPages= listOf(
    OnboardingPage("")
)

@Composable
fun OnboardingScreen(page: OnboardingPage){
    Column(
        modifier=Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = page.image), contentDescription = "image")
        Image(painter = painterResource(id = page.title), contentDescription = "Title")
        Text(text = page.description, style = MaterialTheme.typography.subtitle1)
    }


}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPageViewer(pages:List<OnboardingPage>){
    val state= rememberPagerState(0)

    HorizontalPager(pageCount = 4){page->
        OnboardingScreen(pages[page])
    }

}
@Composable
fun onBoardingPage(navHostController: NavHostController){

}