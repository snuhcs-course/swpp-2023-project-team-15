// MainActivity.kt
package com.example.eatandtell

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eatandtell.ui.Logo
import com.example.eatandtell.ui.appmain.AppMainActivity
import com.example.eatandtell.ui.start.StartActivity
import com.example.eatandtell.ui.theme.EatAndTellTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EatAndTellTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Logo()
                        Spacer(modifier = Modifier.height(16.dp))

                        LaunchedEffect(key1 = Unit) {
                            // 기다릴 시간(1초)
                            delay(1000)

                            val tokenInfo = SharedPreferencesManager.getToken(this@MainActivity)
                            val t = tokenInfo["Token"]
                            Log.d("shared getting token", "Token: $t")
                            if (t != "") {
                                // 1초 후에 appmain으로 이동
                                startActivity(Intent(this@MainActivity, AppMainActivity::class.java).putExtra("Token", t))
                                finish()
                            }
                            else {
                                // 1초 후에 login으로 이동
                                startActivity(Intent(this@MainActivity, StartActivity::class.java))
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }
}
