// MainActivity.kt
package com.swpp2023.eatandtell.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.swpp2023.eatandtell.R
import com.swpp2023.eatandtell.dto.PostDTO
import com.swpp2023.eatandtell.ui.theme.Black
import com.swpp2023.eatandtell.ui.theme.EatAndTellTheme
import com.swpp2023.eatandtell.ui.theme.Gray
import com.swpp2023.eatandtell.ui.theme.Inter
import com.swpp2023.eatandtell.ui.theme.MainColor
import com.swpp2023.eatandtell.ui.theme.PaleOrange
import com.swpp2023.eatandtell.ui.theme.White

public fun showToast(context: Context, message: String?) {
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
fun HeartFull(onClick: (Int) -> Unit, post_id: Int) {
    Icon(
        painter = painterResource(R.drawable.ic_heart_full),
        modifier = Modifier
            .width(24.dp)
            .height(24.dp)
            .clickable { onClick(post_id) }
            .testTag("heart_full")
        ,
        contentDescription = "heart_full",
        tint = MainColor,
    )
}

@Composable
fun HeartEmpty(onClick: (Int) -> Unit, post_id: Int) {
    Icon(
        painter = painterResource(R.drawable.ic_heart_empty),
        modifier = Modifier
            .width(24.dp)
            .height(24.dp)
            .clickable { onClick(post_id) }
            .testTag("heart_empty")
        ,
        contentDescription = "heart_empty",
        tint = MainColor
    )
}

//text fields

@OptIn(ExperimentalMaterial3Api::class,ExperimentalComposeUiApi::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    maxLines : Int = 1,
    enable : Boolean = true,
    testTag: String = "CustomTextField",
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = value,
        onValueChange = { newValue ->
            onValueChange(newValue)
            if (maxLines == 1 && newValue.endsWith("\n")) { // 엔터 키가 눌린 경우
                keyboardController?.hide() // 키보드 숨기기
                onValueChange(newValue.trim()) // 엔터 문자 제거
            }
        },
        visualTransformation = visualTransformation,
        trailingIcon = {
            Box(modifier = Modifier.clickable { onTrailingIconClick?.invoke() }) {
                trailingIcon?.invoke()
            }
        },
        enabled = enable,
        modifier = Modifier
            .border(
                width = 0.5.dp,
                color = Color(0xFFC5C5C5),
                shape = RoundedCornerShape(size = 4.dp)
            )
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .testTag(testTag),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = White , //Color(0xFFEEEEEE),
            cursorColor = Color.Black,
            focusedIndicatorColor = MainColor, //Color(0xFFA0A0A0),
            unfocusedIndicatorColor = Color.Transparent,
            ),
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium
        ) },
        textStyle = MaterialTheme.typography.bodyMedium,
        maxLines = maxLines,

    )
}

@Preview
@Composable
fun PreviewCustomTextField() {
    EatAndTellTheme {
        CustomTextField(
            value = "테스트",
            onValueChange = { /**/ },
            placeholder = "테스트",
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhiteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier,
    width: Dp? = null,
    height: Dp? = null,
    textStyle: TextStyle = TextStyle(
        fontFamily = Inter,
        fontSize = 14.sp,
        fontWeight = FontWeight(400),
        color = Black,
    )
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        visualTransformation = VisualTransformation.None,
        modifier = modifier
            .then(
                if (width != null && height != null) {
                    Modifier.size(width, height)
                } else {
                    Modifier
                }
            ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = White,
            cursorColor = Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        placeholder = {
            Text(
                placeholder,
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(400),
                    color = Gray,
                )
            )
        },
        textStyle = textStyle,
    )
}

@Preview
@Composable
fun PreviewWhiteTextField() {
    EatAndTellTheme {
        WhiteTextField(
            value = "테스트",
            onValueChange = { /**/ },
            placeholder = "테스트",
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
    }
}

@Composable
fun GraySmallText(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = FontFamily.Default,
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
            fontFamily = FontFamily.Default,
            fontSize = 12.sp,
            fontWeight = FontWeight(500),
            color = Black,
        ),
        modifier = modifier ?: Modifier
    )
}


@Composable
fun MainButton(onClick: () -> Unit, text: String, notLoading : Boolean = true, enabled : Boolean = true, containerColor: Color = MainColor, modifier: Modifier = Modifier ) {
    Button(
        onClick = {if (notLoading) onClick() else { /**/ }},
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = White
        ),
        shape = RoundedCornerShape(size = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
    ) {
        if (notLoading) Text(text, color = White, style = TextStyle(
            fontSize = 16.sp,
            lineHeight = 18.sp,
            fontFamily = Inter,
            fontWeight = FontWeight(700),
            color = Color.Black,
        )
        )
        else //show loading
            CircularProgressIndicator(
                color = White,
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
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
        MediumWhiteButton(onClick = { /**/ }, text = "사진 추가하기")
    }
}

@Composable
fun CustomButton(
    onClick: () -> Unit,
    text: String,
    textColor: Color = Black,
    fontWeight: Int = 500,
    containerColor: Color = MainColor, // Pass the container color as a parameter
    borderColor: Color = Color.Transparent,
    cornerRadius: Dp = 40.dp, // You can specify the corner radius
    height: Dp = 36.dp, // You can specify the height
    widthFraction: Float = 0.9f, // Default to 1f which is full width
    icon: ImageVector? = null, // Icon is optional
    testTag: String = "CustomButton",
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
        ),
        shape = RoundedCornerShape(cornerRadius),
        enabled = true,
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .testTag(testTag),
        border = BorderStroke(3.dp,borderColor),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text,
            color = textColor,
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 16.sp,
                fontWeight = FontWeight(fontWeight),
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier
                .padding(0.dp)
                .align(Alignment.CenterVertically)
        )
        // If an icon is provided, display it
        icon?.let {
            Spacer(modifier = Modifier.width(10.dp)) // Add space between text and icon
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh Icon",
                tint = White
            )
        }
    }
}


@Preview
@Composable
fun PreviewCustomButton() {
    EatAndTellTheme {
        CustomButton(onClick = { /**/ }, text = "태그 갱신", containerColor = Gray, borderColor = White)
    }
}


@Composable
fun MediumRedButton(onClick: () -> Unit, text: String, enable: Boolean = true) {
    Button(
        onClick = {if (enable) onClick() else { /**/ }},
        colors = ButtonDefaults.buttonColors(
            containerColor = MainColor,
            contentColor = White
        ),
        shape = RoundedCornerShape(size = 10.dp),
        modifier = Modifier
            .width(120.dp)
            .height(36.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        if (enable) Text(text, color = White,
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            ), modifier = Modifier
                .padding(0.dp)
                .align(Alignment.CenterVertically) // Center the text vertically
        )
        else //show loading
            CircularProgressIndicator(
                color = White,
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
            )
    }
}
@Preview
@Composable
fun PreviewMediumRedButton() {
    EatAndTellTheme {
        MediumRedButton(onClick = { /**/ }, text = "팔로우하기")
    }
}
@Composable
fun LargeRedButton(onClick: () -> Unit, text: String, enable: Boolean = true) {
    Button(
        onClick = {if (enable) onClick() else { /**/ }},
        colors = ButtonDefaults.buttonColors(
            containerColor = MainColor,
            contentColor = White
        ),
        shape = RoundedCornerShape(size = 10.dp),
        modifier = Modifier
            .width(300.dp)
            .height(50.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        if (enable) Text(text, color = White,
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            ), modifier = Modifier
                .padding(0.dp)
                .align(Alignment.CenterVertically) // Center the text vertically
        )
        else //show loading
            CircularProgressIndicator(
                color = White,
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
            )
    }
}

@Preview
@Composable
fun LargeRedButton() {
    EatAndTellTheme {
        LargeRedButton(onClick = { /**/ }, text = "팔로우하기")
    }
}
@Composable
fun Tag(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(color = PaleOrange, shape = RoundedCornerShape(size = 18.dp))
            .border(2.dp, MainColor, RoundedCornerShape(size = 18.dp))
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clickable (onClick=onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Black,
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 16.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight(500),
                color = Color.Black,
            )
        )
    }
}

@Preview
@Composable
fun PreviewTag() {
    EatAndTellTheme {
        Tag(text = "#육식주의자",onClick={/**/})
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
    val hasHalfStar = false // rate - fullStars >= 0.5

    Row(
        horizontalArrangement = Arrangement.spacedBy(0.dp), // 조절 가능한 간격
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Full stars
        repeat(fullStars) {
            StarFull(size)
        }

        // Half star
//        if (hasHalfStar) {
//            StarHalf(size)
//        }

        // Empty stars
        repeat(5 - fullStars - if (hasHalfStar) 1 else 0) {
            StarEmpty(size)
        }
    }
}

@Composable
fun DraggableStarRating(currentRating: Int, onRatingChanged: (Int) -> Unit) {
    //TODO: 0.5점은 구현이 안 됐음. 만약 정수 단위로 평점을 줄 거라면 그냥 int로 받아오면 될 듯
        Row {
            for (i in 1..5) {
                Icon(
                    painter = if (i <= currentRating) painterResource(R.drawable.ic_star_filled)
                                else painterResource(R.drawable.ic_star_empty),
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                        .clickable { onRatingChanged(i) },
                    contentDescription = "star",
                    tint = MainColor,
                )
            }
        }
    }

@Composable
fun ProfileImage(
    profileUrl: String,
    modifier: Modifier = Modifier,
    size : Dp = 45.dp
) {

    Image(
        painter = rememberImagePainter(
            data = profileUrl,
            builder = {
                transformations(CircleCropTransformation())
            }
        ),
        contentDescription = null,
        modifier = modifier
            .border(
                width = 2.dp,
                color = Color(0xFFF23F18),
                shape = RoundedCornerShape(size = 100.dp)
            )
            .padding(2.dp)
            .width(size)
            .height(size)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(size = 100.dp)
            )
    )
}

@Composable
fun EditProfileImage(
    profileUrl: String,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit = { },
    size : Dp = 45.dp
) {

    Image(
        painter = rememberImagePainter(
            data = profileUrl,
            builder = {
                transformations(CircleCropTransformation())
            }
        ),
        contentDescription = null,
        modifier = modifier
            .border(
                width = 2.dp,
                color = Color(0xFFF23F18),
                shape = RoundedCornerShape(size = 100.dp)
            )
            .padding(2.dp)
            .width(size)
            .height(size)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(size = 100.dp)
            )
            .clickable(onClick = onEditClick)
    )
}

@Composable
fun ProfileText(
    username: String,
    userDescription: String,
) {
    Column {
        Text(
            text = username,
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF262626)
            ),
        )
        Text(
            text = userDescription,
            style = TextStyle(
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF848484)
            ),
            modifier = Modifier
                .width(145.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun Profile(
    profileUrl: String,
    username: String,
    userDescription: String,
    onClick: (() -> Unit) = { },
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = onClick)
            .testTag("profile_row")
    ) {
        ProfileImage(profileUrl = profileUrl)
        ProfileText(username = username, userDescription = userDescription)
    }
}
@Composable
fun FollowText(count: Int, label: String,onClick: (() -> Unit) = { },) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)) {
        Text(text = "$count", style = TextStyle(
            fontSize = 16.sp,
            lineHeight = 18.sp,
            fontFamily = Inter,
            fontWeight = FontWeight(700),
            color = Color.Black,
        ))
        Text(text = label, style = TextStyle(
            fontSize = 16.sp,
            lineHeight = 18.sp,
            fontFamily = Inter,
            fontWeight = FontWeight(500),
            color = Color.Black,
        ))
    }
}



// Post Photos
@Composable
fun PostImage(imageUrl: String? = null, onImageClick: () -> Unit) {
    Image(
        painter = (if(imageUrl!=null) rememberImagePainter(data = imageUrl,)
            else painterResource(R.drawable.default_image)), //added default image
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .width(150.dp)
            .height(150.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onImageClick() }
    )
}

@Composable
fun ImageDialog(imageUrl: String, onClick: () -> Unit) {
    Dialog(
        onDismissRequest = { onClick() },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),

    ) {
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "back",
                    tint = Black,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.End)
                        .background(Color(0x55FFFFF), shape=CircleShape)
                        .clickable { onClick() }
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = rememberImagePainter(data = imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color.White)
                )

            }
        }
    }
}

@Preview
@Composable
fun ImageDialogPreview() {
    EatAndTellTheme {
        ImageDialog(
            imageUrl = "https://newprofilepic.photo-cdn.net//assets/images/article/profile.jpg?90af0c8",
            onClick = { /**/ }
        )
    }
}


@Composable
fun Post(
    post : PostDTO,
    onHeartClick: (Int) -> Unit = { },
    canDelete: Boolean = false,
    onDelete : (Int) -> Unit = { },
) {
    val post_id = post.id
    val restaurantName = post.restaurant.name
    val rating = post.rating
    //get list of photo urls from post.photos list's photo_url
    val imageUrls = if (post.photos!=null) post.photos.map { photo -> photo.photo_url } else listOf()
    val description = post.description
    var clickedImageIndex by remember { mutableStateOf(-1) }
    var isLiked by remember { mutableStateOf(post.is_liked) }
    var likes by remember { mutableStateOf(post.like_count) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            //식당 이름
            Text(text = restaurantName, style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight(700),
                color = Black,
            ), modifier = Modifier
                .weight(1f)
                .height(22.dp),
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(4.dp))

            //ratings
            Row(
                horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .wrapContentWidth(Alignment.End)
                    .height(22.dp)
            ) {
                StarRating(rating, size = 18.dp)
            }
        }

        Spacer(modifier = Modifier.height(7.dp))

        // Images Row
        if (imageUrls.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .height(160.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                for ((index, imageUrl) in imageUrls.withIndex()) {
                    PostImage(imageUrl, onImageClick = { clickedImageIndex = index }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Restaurant Description
        Text(text = description, style = TextStyle(
            fontSize = 14.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF262626),
        ), modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
            overflow = TextOverflow.Ellipsis)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            if (canDelete) {
                //show delete button
                MenuWithDropDown(modifier =
                    Modifier.
                    align(Alignment.CenterVertically),
                    onClick = { onDelete(post_id) }
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f),
            ) {
                Text(
                    text = likes.toString(),
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 16.5.sp,
                        fontWeight = FontWeight(500),
                        color = MainColor,
                    ),
                    modifier = Modifier
                        .width(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                if (isLiked) HeartFull(onClick = {
                    onHeartClick(post_id)
                    isLiked = !isLiked
                    likes = likes - 1
                }, post_id = post_id)
                else HeartEmpty(onClick = {
                    onHeartClick(post_id)
                    isLiked = !isLiked
                    likes = likes + 1
                }, post_id = post_id)
            }
        }

//        Spacer(modifier = Modifier.height(24.dp))
    }

    //If Image Clicked, show Image Dialog
    if (clickedImageIndex != -1) {
        ImageDialog(imageUrl = imageUrls[clickedImageIndex] , onClick = { clickedImageIndex = -1 })
    }
}

@Composable
fun MenuWithDropDown(modifier: Modifier, onClick: () -> Unit = { /**/ }) {
    var isDropDownExpanded by remember { mutableStateOf(false) }
    Icon (
        imageVector = Icons.Default.Delete,
        contentDescription = "delete",
        tint = Gray,
        modifier = modifier
            .height(18.dp)
            .clickable(onClick = {
                isDropDownExpanded = !isDropDownExpanded
                })
    )

    DropdownMenu(
        expanded = isDropDownExpanded,
        onDismissRequest = { isDropDownExpanded = false },
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min)
            .padding(0.dp)
    ) {
        DropdownMenuItem(
            text = { Text("삭제", modifier = Modifier.fillMaxWidth(),textAlign = TextAlign.Center)},
            onClick = { onClick(); isDropDownExpanded = false },
            modifier = Modifier.height(32.dp).width(84.dp)
        )
    }

}



@Composable
fun Home(onClick: () -> Unit) {
    Icon(
        painter = painterResource(R.drawable.ic_home),
        modifier = Modifier
            .padding(1.dp)
            .width(24.dp)
            .height(24.dp)
            .clickable(onClick = onClick)
            .testTag("go_to_home"),
        contentDescription = "Home",
        tint = Black
    )
}

@Composable
fun PlusCircle(onClick: () -> Unit) {
    Icon(
        painter = painterResource(R.drawable.ic_plus_circle),
        modifier = Modifier
            .padding(1.dp)
            .width(24.dp)
            .height(24.dp)
            .clickable(onClick = onClick)
            .testTag("go_to_upload"),
    contentDescription = "plus_circle",
        tint = Black
    )
}
@Composable
fun SearchRefraction(onClick: () -> Unit) {
    Icon(
        painter = painterResource(R.drawable.ic_search_refraction),
        modifier = Modifier
            .padding(1.dp)
            .width(24.dp)
            .height(24.dp)
            .clickable(onClick = onClick)
            .testTag("go_to_search"),
        contentDescription = "search_refraction",
        tint = Black
    )
}

@Composable
fun MyIcon(onClick: () -> Unit) {
    Icon(
        imageVector = Icons.Outlined.Person,
        modifier = Modifier
            .padding(1.dp)
            .width(24.dp)
            .height(24.dp)
            .clickable(onClick = onClick)
            .testTag("go_to_profile"),
        contentDescription = "my_home",
        tint = Black
    )
}


@Preview
@Composable
fun UpButton(onClick: () -> Unit = {}) {
    SmallFloatingActionButton(
        onClick = onClick,
        containerColor = MainColor,
        contentColor = White,
        content = {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Scroll to Top"
            )
        },
        //shape is circle
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .padding(bottom = 70.dp, end = 20.dp)
            .fillMaxSize()
            .wrapContentSize(Alignment.BottomEnd)
    )
}

@Composable
fun SearchSelectButton(
    onClick: () -> Unit,
    text: String,
    selected: Boolean,
) {
    val buttonSizeModifier = Modifier.size(width = 102.dp, height = 36.dp) // 버튼 크기 조절

    Box(
        modifier = buttonSizeModifier,
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            MediumWhiteButton(onClick = { onClick() }, text = text)
        } else {
            MediumRedButton(onClick = { onClick() }, text = text)
        }
    }
}