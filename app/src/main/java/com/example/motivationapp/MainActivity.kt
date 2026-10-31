package com.example.motivationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.motivationapp.data.TaskDatabase
import com.example.motivationapp.data.TaskRepository
import com.example.motivationapp.ui.TaskScreen
import com.example.motivationapp.ui.theme.MotivationAppTheme
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