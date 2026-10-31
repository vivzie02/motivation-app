package com.example.motivationapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.motivationapp.viewmodel.TaskViewModel

@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    taskViewModel: TaskViewModel
) {
    var selectedTask by remember {
        mutableStateOf("Tap the button to get an idea")
    }

    var newTask by remember {
        mutableStateOf("")
    }

    val taskList by taskViewModel.tasks.collectAsState()

    Column(modifier = modifier) {
        Text(
            text = "Make today count"
        )
        Text(
            text = "Choose something better to do with your time"
        )
        //Get a random task from list
        Button(
            onClick = {
                selectedTask = taskList.randomOrNull()?.name ?: "Add some tasks first"
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
        //Submit new task
        Button(
            onClick = {
                taskViewModel.addTask(newTask)
                newTask = ""
            }
        ){
            Text(
                text = "Submit"
            )
        }

        //show all current tasks
        Text(text = "Your tasks:")

        taskList.forEach { task ->
            Row {
                Text(text = task.name)
                Button(
                    onClick = {
                        //Remove this task
                        taskViewModel.removeTask(task)
                        if(task.name == selectedTask){
                            selectedTask = ""
                        }
                    }
                ) {
                    Text(
                        text = "Remove"
                    )
                }
            }
        }
    }
}