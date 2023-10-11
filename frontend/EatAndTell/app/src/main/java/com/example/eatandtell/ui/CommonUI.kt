// MainActivity.kt
package com.example.eatandtell.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatandtell.R
import com.example.eatandtell.ui.login.LoginActivity
import com.example.eatandtell.ui.theme.EatAndTellTheme
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
        tint = Color(0xFFF23F18)
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
        tint = Color(0xFFF23F18)
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
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        modifier = modifier,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color(0xFFEEEEEE),
            cursorColor = Color.Black,
            focusedIndicatorColor = Color(0xFFA0A0A0),
            unfocusedIndicatorColor = Color.Transparent,

            ),
        placeholder = { Text(placeholder, style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight(400),
            color = Color(0xFF000000)
        )
        ) },
        supportingText = { Text(supportingText, style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight(400),
            color = Color(0xFF000000)
        )
        ) },
        maxLines = 1
    )
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
        tint = Color(0xFFF23F18)
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
        tint = Color(0xFFF23F18)
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
        tint = Color(0xFFF23F18)
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