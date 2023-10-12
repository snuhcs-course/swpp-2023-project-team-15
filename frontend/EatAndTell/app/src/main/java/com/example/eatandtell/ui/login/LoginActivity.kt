// RegisterActivity.kt
package com.example.eatandtell.ui.login
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.eatandtell.R
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.eatandtell.ui.home.HomeActivity

import com.example.eatandtell.ui.signup.SignupScreen

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.viewModels
import com.example.eatandtell.ui.BlackSmallText
import com.example.eatandtell.ui.CustomTextField
import com.example.eatandtell.ui.GraySmallText
import com.example.eatandtell.ui.Logo
import com.example.eatandtell.ui.MainButton
import com.example.eatandtell.ui.login.LoginViewModel
import com.example.eatandtell.ui.showToast
import com.example.eatandtell.ui.signup.RegisterActivity
import com.example.eatandtell.ui.signup.RegisterViewModel
import com.example.eatandtell.ui.theme.Black
import com.example.eatandtell.ui.theme.Gray
import com.example.eatandtell.ui.uploadpost.UploadActivity


class LoginActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 20.dp),
                    color = MaterialTheme.colorScheme.background,

                ) {
                    LoginScreen(this@LoginActivity, loginViewModel)
                }
            }
        }
    }
}

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
fun LoginScreen(context: ComponentActivity, viewModel: LoginViewModel) {

    var id by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var password by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    // Main content of LoginActivity
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
                Log.d("login activity", "ID: ${id.text}, Password: ${password.text}")
                context.startActivity(Intent(context, HomeActivity::class.java))
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
                    context.startActivity(
                        Intent(context, RegisterActivity::class.java)
                    )
                }
            )

        }
    }
}

@Composable
fun LoginButton(viewModel: LoginViewModel, id: String, password: String, context: Context, onClick: () -> Unit) {
    val onClickReal = {
        viewModel.loginUser(id, password, object: LoginViewModel.LoginCallback{
            override fun onLoginSuccess(token: String?) {
                onClick()
            }
            override fun onLoginError(errorMessage: String) {
                showToast(context, errorMessage)
            } } )}
    MainButton(onClickReal, "로그인")
}

