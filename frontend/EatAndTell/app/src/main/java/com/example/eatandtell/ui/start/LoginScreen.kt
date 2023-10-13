// SignUpScreen.kt
package com.example.eatandtell.ui.start
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.eatandtell.R
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavController
import com.example.eatandtell.ui.appmain.AppMainActivity

import com.example.eatandtell.ui.BlackSmallText
import com.example.eatandtell.ui.CustomTextField
import com.example.eatandtell.ui.GraySmallText
import com.example.eatandtell.ui.Logo
import com.example.eatandtell.ui.MainButton
import com.example.eatandtell.ui.showToast

@Composable
fun PasswordVisibilityToggle(passwordHidden: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        val visibilityIcon = if (passwordHidden) {
            painterResource(R.drawable.ic_visibility_off)
        } else {
            painterResource(R.drawable.ic_visibility)
        }
        Icon(painter = visibilityIcon, contentDescription = "visibility")
    }
}

@Composable
fun LoginScreen(navController: NavController, context: ComponentActivity, viewModel: StartViewModel) {

    var id by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var password by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    // Main content
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Logo()
        Spacer(modifier = Modifier.height(18.dp))
        CustomTextField(
            value = id.text,
            onValueChange = { id = TextFieldValue(it) },
            placeholder = "아이디를 입력하세요",
        )
        Spacer(modifier = Modifier.height(12.dp))

        CustomTextField(
            value = password.text,
            onValueChange = { password = TextFieldValue(it) },
            visualTransformation =
            if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                PasswordVisibilityToggle(passwordHidden) {
                    passwordHidden = !passwordHidden
                }
            },
            placeholder = "비밀번호를 입력하세요",
            )
        Spacer(modifier = Modifier.height(12.dp))

        LoginButton(
            onClick = {
                Log.d("login screen", "ID: ${id.text}, Password: ${password.text}")
                context.startActivity(Intent(context, AppMainActivity::class.java))
                context.finish()
            },
            id = id.text,
            password = password.text,
            context = context,
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            GraySmallText(
                text = "계정 정보를 잊어버리셨나요?"
            )
            Spacer(modifier = Modifier.width(4.dp))
            BlackSmallText(
                text = "아이디/패스워드 찾기",
                modifier = Modifier.clickable { /* 여기에 클릭 시 수행될 동작 추가 */ }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            GraySmallText(
                text = "계정이 없으십니까?",
            )
            Spacer(modifier = Modifier.width(4.dp))
            BlackSmallText(
                text = "회원가입",
                modifier = Modifier.clickable {
                    navController.navigate("signup")
                }
            )

        }
    }
}

@Composable
fun LoginButton(viewModel: StartViewModel, id: String, password: String, context: Context, onClick: () -> Unit) {
    val onClickReal = {
        viewModel.loginUser(id, password, object: StartViewModel.LoginCallback{
            override fun onLoginSuccess(token: String?) {
                onClick()
            }
            override fun onLoginError(errorMessage: String) {
                showToast(context, errorMessage)
            } } )}
    MainButton(onClickReal, "로그인")
}

