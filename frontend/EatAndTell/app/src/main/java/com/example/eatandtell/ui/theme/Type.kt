package com.example.eatandtell.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val MyTypography = Typography(
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 10.sp,
        fontWeight = FontWeight(400),
        color = Gray,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 30.sp,
        fontWeight = FontWeight(700),
        color = Black,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 6.sp,
        fontWeight = FontWeight(400),
        color = MainColor
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)