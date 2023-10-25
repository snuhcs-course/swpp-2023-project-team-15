// SignUpScreen.kt
package com.example.eatandtell.ui.start
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eatandtell.ui.BlackSmallText
import com.example.eatandtell.ui.CustomTextField
import com.example.eatandtell.ui.GraySmallText
import com.example.eatandtell.ui.Logo
import com.example.eatandtell.ui.MainButton
import com.example.eatandtell.ui.appmain.AppMainActivity
import com.example.eatandtell.ui.showToast

@Composable
fun SignupScreen(navController: NavController, context: ComponentActivity, viewModel: StartViewModel) {

    var email by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var username by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var password by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var confirmPassword by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    // Main content of SignupActivity
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)
    ) {
        Logo()
        Spacer(modifier = Modifier.height(17.dp))

        CustomTextField(
            value = email.text,
            onValueChange = { email = TextFieldValue(it) },
            placeholder = "이메일을 입력하세요",
        )
        Spacer(modifier = Modifier.height(12.dp))

        CustomTextField(
            value = username.text,
            onValueChange = { username = TextFieldValue(it) },
            placeholder = "아이디를 입력하세요 (4~20자)",
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
            placeholder = "비밀번호를 입력하세요 (4~20자)",
        )
        Spacer(modifier = Modifier.height(12.dp))

        CustomTextField(
            value = confirmPassword.text,
            onValueChange = { confirmPassword = TextFieldValue(it) },
            visualTransformation =
            if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                PasswordVisibilityToggle(passwordHidden) {
                    passwordHidden = !passwordHidden
                }

            },
            placeholder = "비밀번호 확인",
        )
        Spacer(modifier = Modifier.height(12.dp))

        SignupButton(
            onClick =  { token ->
                val intent = Intent(context, AppMainActivity::class.java)
                intent.putExtra("Token", token) // 토큰 넘겨주기
                context.startActivity(intent)
                context.finish()
            },
            email = email.text,
            username = username.text,
            password = password.text,
            confirmPassword = confirmPassword.text,
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
                text = "이미 계정이 있으십니까?",
            )
            Spacer(modifier = Modifier.width(4.dp))
            BlackSmallText(
                text = "로그인",
                modifier = Modifier.clickable {
                    navController.navigate("login")
                }
            )
        }
    }
}



@Composable
fun SignupButton (
    viewModel: StartViewModel,
    onClick: (String?) -> Unit,
    email: String,
    username: String,
    password: String,
    confirmPassword: String,
    context: Context
) {
    val emailRegex = Regex("^\\S+@\\S+\\.\\S+\$")

    fun isEmailValid(email: String): Boolean {
        return emailRegex.matches(email)
    }

    val onClickReal = {
        when {
            email.isBlank() -> showToast(context, "이메일을 입력하세요")
            !isEmailValid(email) -> showToast(context, "이메일 주소가 올바르지 않습니다")
            username.isBlank() -> showToast(context, "아이디를 입력하세요")
            username.length !in 4..20 -> showToast(context, "아이디가 올바르지 않습니다")
            password.isBlank() -> showToast(context, "비밀번호를 입력하세요")
            password.length !in 4..20 -> showToast(context, "비밀번호가 올바르지 않습니다")
            confirmPassword.isBlank() -> showToast(context, "비밀번호 확인을 입력하세요")
            password != confirmPassword -> showToast(context, "비밀번호 확인이 틀립니다")
            else -> {
                viewModel.registerUser(username, password, email, context, onSuccess = onClick)
            }
        }

    }

    MainButton(onClick = onClickReal, text = "회원가입")
}