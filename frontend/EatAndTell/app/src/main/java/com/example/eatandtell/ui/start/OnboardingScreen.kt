package com.example.eatandtell.ui.start

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.eatandtell.R
import com.example.eatandtell.dto.OnboardingPage
import com.example.eatandtell.ui.Logo
import kotlinx.coroutines.delay


class OnBoardingViewModel: ViewModel(){
    var currentPage: Int by mutableStateOf(0)

    fun setPage(page:Int){
        currentPage= page
    }
}

@Composable
fun OnboardingBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xF9A3654F),
                radius = size.minDimension / 2 ,
                center = Offset(x = size.width * 0.75f, y = size.height*0.7f)
            )
            drawCircle(
                color = Color(0xF23F184F), // Use the color code for the orange circle
                radius = (size.minDimension / 2.6).toFloat(),
                center = Offset(x = size.width * 1.33f, y = size.height * 0.5f)
            )
            drawCircle(
                color = Color(0xF23F184F), // Use the color code for the orange circle
                radius = (size.minDimension / 2.6).toFloat(),
                center = Offset(x = size.width * -0.05f, y = size.height * 0.05f)
            )

        }
    }
}


@Composable
fun OnboardingScreen1( ){

    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingBackground()
        // Content layered on top of the background
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo Image",
                modifier = Modifier
                    .width(330.dp)
                    .height(30.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("오직 맛집 리뷰만, 당신이 기다려온 SNS", style = MaterialTheme.typography.h2)
            Spacer(modifier = Modifier.height(32.dp))

        }
    }
}

@Composable
fun OnboardingScreen2() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo Image",
                modifier = Modifier
                    .width(245.dp)
                    .height(30.dp)

            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("태그,\nAI가 찾아주는 나만의 취향",style = MaterialTheme.typography.h3)

            Image(
                painter = painterResource(id = R.drawable.onboarding2),
                contentDescription = "tag description",
                modifier= Modifier
                    .width(280.dp)
                    .height(220.dp)
            )
        }

    }

}


@Composable
fun ImageCrossfade() {
    var isImageA by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            isImageA = !isImageA
        }
    }

    val currentImage = if (isImageA) R.drawable.onboarding3_a else R.drawable.onboarding3_b

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Crossfade(targetState = currentImage, animationSpec = tween(durationMillis = 1000),
            label = ""
        ) { targetImage ->
            Image(
                painter = painterResource(id = targetImage),
                contentDescription = "Clcik animation",
                modifier = Modifier
                    .fillMaxWidth()

            )
        }
    }
}

@Composable
fun onBoardingScreen3(){

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo Image",
                modifier = Modifier
                    .width(245.dp)
                    .height(30.dp)

            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("태그,\nAI가 찾아주는 나만의 취향",style = MaterialTheme.typography.h3)

            ImageCrossfade()
        }

    }


}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPageViewer(){
    val state= rememberPagerState(0)

    HorizontalPager(pageCount = 4){page->
        if(page==3){
            OnboardingScreen_typeB(page =pages[page])
        }
        else{
            OnboardingScreen_typeB(page =pages[page])
        }
    }

}
