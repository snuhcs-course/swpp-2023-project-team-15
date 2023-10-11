// RegisterActivity.kt
package com.example.eatandtell.ui.signup
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eatandtell.Logo
import com.example.eatandtell.R
import com.example.eatandtell.ui.login.CustomTextField
import com.example.eatandtell.ui.login.LoginActivity
import com.example.eatandtell.ui.login.LoginScreen
import com.example.eatandtell.ui.login.PasswordVisibilityToggle

class RegisterActivity : ComponentActivity() {
    private val registerViewModel: RegisterViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

                SignupScreen(this@RegisterActivity)

            }
        }

    }
}

@Composable
fun SignupScreen(context: ComponentActivity) {

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
        verticalArrangement = Arrangement.Center
    ) {
        Logo()
        Spacer(modifier = Modifier.height(17.dp))

        CustomTextField(
            value = email.text,
            onValueChange = { email = TextFieldValue(it) },
            placeholder = "이메일을 입력하세요 (예: name@example.com)",
            modifier = Modifier
                .border(
                    width = 0.5.dp,
                    color = Color(0xFFC5C5C5),
                    shape = RoundedCornerShape(size = 4.dp)
                )
                .width(320.dp)
                .height(48.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))

        CustomTextField(
            value = username.text,
            onValueChange = { username = TextFieldValue(it) },
            placeholder = "아이디를 입력하세요 (4자 이상, 20자 이하)",
            modifier = Modifier
                .border(
                    width = 0.5.dp,
                    color = Color(0xFFC5C5C5),
                    shape = RoundedCornerShape(size = 4.dp)
                )
                .width(320.dp)
                .height(48.dp),
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
            placeholder = "비밀번호를 입력하세요 (4자 이상, 20자 이하)",
            modifier = Modifier
                .border(
                    width = 0.5.dp,
                    color = Color(0xFFC5C5C5),
                    shape = RoundedCornerShape(size = 4.dp)
                )
                .width(320.dp)
                .height(48.dp),
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
            modifier = Modifier
                .border(
                    width = 0.5.dp,
                    color = Color(0xFFC5C5C5),
                    shape = RoundedCornerShape(size = 4.dp)
                )
                .width(320.dp)
                .height(48.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))

        SignupButton(
            onClick =  {
                context.startActivity(Intent(context, LoginActivity::class.java))
                context.finish()
            },

            email = email.text,
            username = username.text,
            password = password.text,
            confirmPassword = confirmPassword.text,
            context = context,
            viewModel=viewModel()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "이미 계정이 있으십니까?",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF6D6D6D),
                ),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "로그인",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFF23244F),
                ),
                modifier = Modifier.clickable {
                    context.finish()
                }
            )

        }
    }
}

@Composable
fun SignupButton (

    viewModel: RegisterViewModel,
    onClick: () -> Unit,
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

    Button(
        onClick = {
            when {
                email.isBlank() -> showToast(context, "Please enter your email")
                !isEmailValid(email) -> showToast(context, "Invalid email address")
                username.isBlank() -> showToast(context, "Please enter your username")
                username.length !in 4..20 -> showToast(context, "Invalid ID")
                password.isBlank() -> showToast(context, "Please enter your password")
                password.length !in 4..20 -> showToast(context, "Invalid password")
                confirmPassword.isBlank() -> showToast(context, "Please confirm your password")
                password != confirmPassword -> showToast(context, "Passwords do not match")
                else -> {
                    viewModel.registerUser(username, password, email, object: RegisterViewModel.RegisterCallback{
                        override fun onRegisterSuccess(token: String?) {
                            onClick()
                        }

                        override fun onRegisterError(errorMessage: String) {
                            showToast(context, errorMessage)
                        }
                    })
                }
            }



        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF23F18),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(size = 4.dp),
        modifier = Modifier
            .width(320.dp)
            .height(48.dp)
    ) {
        Text("Sign Up", color = Color.White)
    }
}

private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    println(message)
}

@Preview
@Composable
fun SignupScreenPreview() {
    val dummyActivity = ComponentActivity()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SignupScreen(dummyActivity)
    }
}