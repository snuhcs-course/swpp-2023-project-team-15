// MainActivity.kt
package com.example.eatandtell.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.example.eatandtell.R
import com.example.eatandtell.ui.login.LoginActivity
import com.example.eatandtell.ui.signup.RegisterViewModel
import com.example.eatandtell.ui.theme.Black
import com.example.eatandtell.ui.theme.EatAndTellTheme
import com.example.eatandtell.ui.theme.MainColor
import com.example.eatandtell.ui.theme.White
import kotlinx.coroutines.delay

public fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    println(message)
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
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodySmall
        ) },
        supportingText = { Text(supportingText, style = MaterialTheme.typography.bodySmall
        ) },
        maxLines = 1
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
        modifier = Modifier.width(320.dp).height(48.dp),
    ) {
        Text(text, color = White)
    }
}


// stars, ratings

@Composable
fun StarFull() {
    Icon(
        painter = painterResource(R.drawable.ic_star_filled),
        modifier = Modifier
            .width(16.dp)
            .height(16.dp),
        contentDescription = "star_full",
        tint = MainColor
    )
}

@Composable
fun StarEmpty() {
    Icon(
        painter = painterResource(R.drawable.ic_star_empty),
        modifier = Modifier
            .width(16.dp)
            .height(16.dp),
        contentDescription = "star_empty",
        tint = MainColor
    )
}

@Composable
fun StarHalf() {
    Icon(
        painter = painterResource(R.drawable.ic_star_half),
        modifier = Modifier
            .width(16.dp)
            .height(16.dp),
        contentDescription = "star_half",
        tint = MainColor
    )
}

@Composable
fun StarRating(rating: Float) {
    val fullStars = rating.toInt()
    val hasHalfStar = rating - fullStars >= 0.5

    Row(
        horizontalArrangement = Arrangement.spacedBy(0.dp), // 조절 가능한 간격
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Full stars
        repeat(fullStars) {
            StarFull()
        }

        // Half star
        if (hasHalfStar) {
            StarHalf()
        }

        // Empty stars
        repeat(5 - fullStars - if (hasHalfStar) 1 else 0) {
            StarEmpty()
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
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF262626),
            )
        )
        Text(text = userDescription,
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 18.sp,

                fontWeight = FontWeight(500),
                color =Color(0xFF848484)
            )

        )
    }
}

@Composable
fun Profile(profileUrl: String, username: String, userDescription: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
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