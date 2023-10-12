// MainActivity.kt
package com.example.eatandtell.ui

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.example.eatandtell.R
import com.example.eatandtell.ui.theme.Black
import com.example.eatandtell.ui.theme.EatAndTellTheme
import com.example.eatandtell.ui.theme.Gray
import com.example.eatandtell.ui.theme.Inter
import com.example.eatandtell.ui.theme.MainColor
import com.example.eatandtell.ui.theme.White
import com.example.eatandtell.ui.uploadpost.UploadScreen

public fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun Logo() {
    Image(
        painter = painterResource(R.drawable.logo),
        contentDescription = "Logo Image",
        modifier = Modifier
            .width(210.dp)
            .height(30.dp)
    )
}

// hearts

@Composable
fun HeartFull() {
    Icon(
        painter = painterResource(R.drawable.ic_heart_full),
        modifier = Modifier
            .width(24.dp)
            .height(24.dp),
        contentDescription = "heart_full",
        tint = MainColor
    )
}

@Composable
fun HeartEmpty() {
    Icon(
        painter = painterResource(R.drawable.ic_heart_empty),
        modifier = Modifier
            .width(24.dp)
            .height(24.dp),
        contentDescription = "heart_empty",
        tint = MainColor
    )
}

//text fields

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    supportingText: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        modifier = Modifier
            .border(
                width = 0.5.dp,
                color = Color(0xFFC5C5C5),
                shape = RoundedCornerShape(size = 4.dp)
            )
            .width(320.dp)
            .height(48.dp),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color(0xFFEEEEEE),
            cursorColor = Color.Black,
            focusedIndicatorColor = Color(0xFFA0A0A0),
            unfocusedIndicatorColor = Color.Transparent,

            ),
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium
        ) },
        supportingText = { Text(supportingText, style = MaterialTheme.typography.bodyMedium
        ) },
        textStyle = MaterialTheme.typography.bodyMedium,
        maxLines = 1
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhiteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    maxLines : Int = 1,
    modifier : Modifier,
    width : Dp? = null,
    height : Dp? = null,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        visualTransformation = VisualTransformation.None,
        modifier = modifier,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = White,
            cursorColor = Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        placeholder = { Text(placeholder, style = TextStyle(
            fontFamily = Inter,
            fontSize = 14.sp,
            fontWeight = FontWeight(400),
            color = Gray,
        ),
        ) },
        textStyle = TextStyle(
            fontFamily = Inter,
            fontSize = 14.sp,
            fontWeight = FontWeight(400),
            color = Black,
        ),
        maxLines = maxLines

    )
}


@Composable
fun GraySmallText(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = Inter,
            fontSize = 12.sp,
            fontWeight = FontWeight(400),
            color = Gray,
        ),
    )
}

@Composable
fun BlackSmallText(text: String, modifier: Modifier?) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = Inter,
            fontSize = 12.sp,
            fontWeight = FontWeight(500),
            color = Black,
        ),
        modifier = modifier ?: Modifier
    )
}


@Composable
fun MainButton(onClick: () -> Unit, text: String) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MainColor,
            contentColor = White
        ),
        shape = RoundedCornerShape(size = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
    ) {
        Text(text, color = White,
        )
    }
}

@Composable
fun MediumWhiteButton(onClick: () -> Unit, text: String) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = White,
            contentColor = MainColor
        ),
        shape = RoundedCornerShape(size = 10.dp),
        modifier = Modifier
            .width(120.dp)
            .height(36.dp),
        border = BorderStroke(1.dp, MainColor),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text, color = MainColor,
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            ), modifier = Modifier
                .padding(0.dp)
                .align(Alignment.CenterVertically) // Center the text vertically
        )
    }
}

@Preview
@Composable
fun PreviewMediumWhiteButton() {
    EatAndTellTheme {
        MediumWhiteButton(onClick = { /*TODO*/ }, text = "사진 추가하기")
    }
}



// stars, ratings

@Composable
fun StarFull(size : Dp) {
    Icon(
        painter = painterResource(R.drawable.ic_star_filled),
        modifier = Modifier
            .width(size)
            .height(size),
        contentDescription = "star_full",
        tint = MainColor
    )
}

@Composable
fun StarEmpty(size : Dp) {
    Icon(
        painter = painterResource(R.drawable.ic_star_empty),
        modifier = Modifier
            .width(size)
            .height(size),
        contentDescription = "star_empty",
        tint = MainColor
    )
}

@Composable
fun StarHalf(size : Dp) {
    Icon(
        painter = painterResource(R.drawable.ic_star_half),
        modifier = Modifier
            .width(size)
            .height(size),
        contentDescription = "star_half",
        tint = MainColor
    )
}

@Composable
fun StarRating(rating: String, size: Dp = 16.dp) {
    val rate = rating.toFloat()
    val fullStars = rate.toInt()
    val hasHalfStar = rate - fullStars >= 0.5

    Row(
        horizontalArrangement = Arrangement.spacedBy(0.dp), // 조절 가능한 간격
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Full stars
        repeat(fullStars) {
            StarFull(size)
        }

        // Half star
        if (hasHalfStar) {
            StarHalf(size)
        }

        // Empty stars
        repeat(5 - fullStars - if (hasHalfStar) 1 else 0) {
            StarEmpty(size)
        }
    }
}

//profile image
@Composable
fun ProfileImage(profileUrl: String) {
    Image(
        painter = rememberImagePainter(
            data = profileUrl,
            builder = {
                transformations(CircleCropTransformation())
            }
        ),
        contentDescription = null,
        modifier = Modifier
            .border(
                width = 2.dp,
                color = Color(0xFFF23F18),
                shape = RoundedCornerShape(size = 100.dp)
            )
            .padding(2.dp)
            .width(45.dp)
            .height(45.dp)
            .background(
                color = White,
                shape = RoundedCornerShape(size = 100.dp)
            )
    )
}

@Composable
fun ProfileText(username: String, userDescription: String) {
    Column {
        Text(
            text = username,
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF262626),
            )
        )
        Text(text = userDescription,
            style = TextStyle(
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF848484)
            )

        )
    }
}

@Composable
fun Profile(profileUrl: String, username: String, userDescription: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        ProfileImage(profileUrl = profileUrl)
        ProfileText(username = username, userDescription = userDescription)
    }
}


// Post Photos
@Composable
fun PostImage(imageUrl: String) {
    Image(
        painter = rememberImagePainter(
            data = imageUrl,
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .width(150.dp)
            .height(150.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
    )
}

// Top Bar
@Composable
fun BackBar(name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = "Back Button"
            )
        }
        Text(
            text = name,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
            ),
        )

    }
}

@Preview(showBackground = true)
@Composable
fun BackBarPreview() {
     EatAndTellTheme {
        BackBar(name = "리뷰 작성")
    }
}