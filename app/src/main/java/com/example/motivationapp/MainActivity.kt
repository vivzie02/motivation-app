package com.example.motivationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.motivationapp.data.TaskDatabase
import com.example.motivationapp.data.TaskRepository
import com.example.motivationapp.ui.ManageTasksScreen
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

            // Simple two-page navigation: false = slot machine, true = manage tasks.
            var showManageScreen by rememberSaveable { mutableStateOf(false) }

            // System back button / gesture returns to the slot machine.
            BackHandler(enabled = showManageScreen) {
                showManageScreen = false
            }

            if (showManageScreen) {
                ManageTasksScreen(
                    taskViewModel = taskViewModel,
                    onBack = { showManageScreen = false }
                )
            } else {
                TaskScreen(
                    taskViewModel = taskViewModel,
                    onManageTasks = { showManageScreen = true }
                )
            }
        }
    }
}