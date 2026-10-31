package com.example.motivationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.motivationapp.data.TaskDatabase
import com.example.motivationapp.data.TaskRepository
import com.example.motivationapp.ui.TaskScreen
import com.example.motivationapp.viewmodel.TaskViewModel
import com.example.motivationapp.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = TaskDatabase.getDatabase(applicationContext)
        val repository = TaskRepository(database.taskDao())
        val factory = TaskViewModelFactory(repository)

        setContent {
            val taskViewModel: TaskViewModel = viewModel(
                factory = factory
            )

            TaskScreen(taskViewModel = taskViewModel)
        }
    }
}