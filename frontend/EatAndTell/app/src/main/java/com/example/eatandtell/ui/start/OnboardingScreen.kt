package com.example.eatandtell.ui.start

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.eatandtell.R
import com.example.eatandtell.ui.LargeRedButton
import com.example.eatandtell.ui.appmain.AppMainActivity
import com.example.eatandtell.ui.theme.Inter
import kotlinx.coroutines.delay


class OnBoardingViewModel: ViewModel(){
    var currentPage: Int by mutableStateOf(0)

    fun setPage(page:Int){
        currentPage= page
    }
}

@Composable
fun OnboardingBackground() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.9f)
    ) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
        ) {
            drawCircle(
                color = Color(0xFFFCD3B6),
                radius = size.minDimension / 2 ,
                center = Offset(x = size.width * 0.75f, y = size.height*0.3f)
            )
            drawCircle(
                color = Color(0xFFF6B0A0), // Use the color code for the orange circle
                radius = (size.minDimension / 2.6).toFloat(),
                center = Offset(x = size.width * 1.2f, y = size.height * 0.5f)
            )
            drawCircle(
                color = Color(0xFFF6B0A0), // Use the color code for the orange circle
                radius = (size.minDimension / 2.6).toFloat(),
                center = Offset(x = size.width * -0.05f, y = size.height * 0.95f)
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
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo Image",
                modifier = Modifier
                    .width(360.dp)
                    .height(30.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            val text = "오직 맛집 리뷰만, 당신이 기다려 온 SNS"

            val boldText = "맛집"
            val boldText2 = "SNS"

            val indexOfBoldText = text.indexOf(boldText)
            val indexOfBoldText2 = text.indexOf(boldText2)

            Text(
                text = buildAnnotatedString {
                    append(text.substring(0,indexOfBoldText))
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text.substring(indexOfBoldText, indexOfBoldText + boldText.length))
                    }
                    append(text.substring(indexOfBoldText + boldText.length, indexOfBoldText2))
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text.substring(indexOfBoldText2))
                    }
                },
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 36.sp,
                    textAlign = TextAlign.Center,
                )
            )


        }
    }
}

@Composable
fun OnboardingScreen2() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
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

            Text("태그,\nAI가 찾아주는 나만의 취향",style = TextStyle(
                fontFamily = Inter,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )
            )

            Spacer(modifier = Modifier.height(16.dp))
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
fun OnboardingScreen3(){

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
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

            Text("태그,\nAI가 찾아주는 나만의 취향",style = TextStyle(
                fontFamily = Inter,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            ))

            Spacer(modifier = Modifier.height(16.dp))

            var isImageA by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                while (true) {
                    delay(1000)
                    isImageA = !isImageA
                }
            }

            val currentImage = if (isImageA) R.drawable.onboarding3_a else R.drawable.onboarding3_b

            Crossfade(targetState = currentImage, animationSpec = tween(durationMillis = 1500),
                label = ""
            ) { targetImage ->
                Image(
                    painter = painterResource(id = targetImage),
                    contentDescription = "Click animation",
                    modifier = Modifier
                        .width(280.dp)
                        .height(220.dp)
                )
            }
        }






    }


}

@Composable
fun OnboardingScreen4(context: ComponentActivity) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
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

            Text("태그 검색으로\n취향이 비슷한 유저 찾기",style = TextStyle(
                fontFamily = Inter,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            ))

            Image(
                painter = painterResource(id = R.drawable.onboarding4),
                contentDescription = "tag description",
                modifier= Modifier
                    .width(280.dp)
                    .height(220.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            LargeRedButton(onClick = {
                val intent = Intent(context, AppMainActivity::class.java)
                context.startActivity(intent)
                context.finish()
            }, text = "시작하기")

        }

    }

}
@Composable
fun PageIndicator(currentPage: Int, pageCount: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 0 until pageCount) {
            Indicator(isSelected = i == currentPage)
        }
    }
}

@Composable
fun Indicator(isSelected: Boolean) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(10.dp)
            .clip(CircleShape)
            .background(if (isSelected) Color.Blue else Color.LightGray)
    )
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen( context: ComponentActivity){
    val pagerState= rememberPagerState(0)
    Column {

        HorizontalPager(pageCount = 4, state = pagerState) { page ->
            when (page) {
                0 -> OnboardingScreen1()
                1 -> OnboardingScreen2()
                2 -> OnboardingScreen3()
                3 -> OnboardingScreen4(context)


            }

        }
        Spacer(modifier = Modifier.height(16.dp))
        PageIndicator(currentPage = pagerState.currentPage, pageCount =4)
    }
}
