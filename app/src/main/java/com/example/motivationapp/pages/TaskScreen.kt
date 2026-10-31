package com.example.motivationapp.pages

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun TaskScreen(modifier: Modifier = Modifier) {
    var selectedTask by remember{
        mutableStateOf("Tap the button to get an idea")
    }

    var newTask by remember{
        mutableStateOf("")
    }

    val taskList = remember {
        mutableStateListOf(
            "Read",
            "Study",
            "Play piano",
            "Clean"
        )
    }

    Column(modifier = modifier) {
        Text(
            text = "Make today count"
        )
        Text(
            text = "Choose something better to do with your time"
        )
        Button(
            onClick = {
                selectedTask = taskList.random()
                Log.d("MainPage", "Selection tapped - $selectedTask")
            }
        ) {
            Text("Pick something to do")
        }

        Text(
            text = selectedTask
        )

        TextField(
            newTask,
            onValueChange = { inputText ->
                newTask = inputText
            },
            label = {Text("Enter a task")}
        )
        Button(
            onClick = {
                if(newTask.isNotBlank() && !taskList.contains(newTask.trim())){
                    taskList.add(newTask.trim())
                    newTask = ""
                }

            }
        ){
            Text(
                text = "Submit"
            )
        }

        //show all current tasks
        Text(text = "Your tasks:")

        taskList.forEach { task ->
            Text(text = task)
        }
    }
}