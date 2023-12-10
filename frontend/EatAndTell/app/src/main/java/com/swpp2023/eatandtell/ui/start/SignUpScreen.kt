// SignUpScreen.kt
package com.swpp2023.eatandtell.ui.start
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.swpp2023.eatandtell.ui.CustomTextField
import com.swpp2023.eatandtell.ui.GraySmallText
import com.swpp2023.eatandtell.ui.Logo
import com.swpp2023.eatandtell.ui.MainButton
import com.swpp2023.eatandtell.ui.showToast
import com.swpp2023.eatandtell.ui.theme.Black

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

    val registerState by viewModel.registerState

    fun hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(context.currentFocus?.windowToken, 0)
    }

    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                showToast(context, "회원가입이 완료되었습니다.")
                navController.navigate("onboarding") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }


            }
            is RegisterState.Error -> {
                val errorMessage = (registerState as RegisterState.Error).message
                showToast(context,"아이디가 중복되었습니다.")

            }
            // Handle other states if necessary
            else -> Unit
        }
    }

    // Main content of SignupActivity
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { hideKeyboard() }
                )
            }
            .fillMaxSize()
            .padding(horizontal = 20.dp)
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
            onClick =  {it->

                Log.d("register screen", "Username ${username.text}, Password: ${password.text}")
                viewModel.registerUser(username.text, password.text,email.text,context)
                viewModel.resetStates()
            },
            email = email.text,
            username = username.text,
            password = password.text,
            confirmPassword = confirmPassword.text,
            context = context,
        )

        Spacer(modifier = Modifier.height(18.dp))


            GraySmallText(
                text = "이미 계정이 있으십니까?",
            )
        Spacer(modifier = Modifier.height(18.dp))
        MainButton(
                onClick = {
                    navController.navigate("login")
                    viewModel.resetStates()
                          },
                text = "로그인하기",
                containerColor = Black,
                modifier = Modifier.testTag("go_to_login")
            )

    }
}



@Composable
fun SignupButton(
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


    MainButton(text = "회원가입",
        onClick={
            val trimmedEmail = email.trim()
            when {
                trimmedEmail.isBlank() -> showToast(context, "이메일을 입력하세요")
                !isEmailValid(trimmedEmail) -> showToast(context, "이메일 주소가 올바르지 않습니다")
                username.isBlank() -> showToast(context, "아이디를 입력하세요")
                username.length !in 4..20 -> showToast(context, "아이디가 4-20자여야 합니다")
                password.isBlank() -> showToast(context, "비밀번호를 입력하세요")
                password.length !in 4..20 -> showToast(context, "비밀번호가 4-20자여야 합니다")
                confirmPassword.isBlank() -> showToast(context, "비밀번호 확인을 입력하세요")
                password != confirmPassword -> showToast(context, "비밀번호 확인이 일치하지 않습니다")
                else -> {
                    onClick(null)
                }
            }
        })
}
