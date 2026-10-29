package com.example.motivationapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.motivationapp.ui.theme.MotivationAppTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MotivationAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        var selectedTask by remember{
            mutableStateOf("Tap the button to get an idea")
        }

        Text(
            text = "Make today count"
        )
        Text(
            text = "Choose something better to do with your time"
        )
        Button(
            onClick = {
                //Do something
                selectedTask = tasks.random()
                Log.d("MainPage", "Selection tapped - $selectedTask")
            }
        ) {
            Text("Pick something to do")
        }

        Text(
            text = selectedTask
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MotivationAppTheme {
        Greeting()
    }
}

val tasks = listOf(
    "Read",
    "Study",
    "Play piano",
    "Clean"
)