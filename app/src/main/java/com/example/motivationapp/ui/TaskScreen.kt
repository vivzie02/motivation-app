package com.example.motivationapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.motivationapp.viewmodel.TaskViewModel

@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    taskViewModel: TaskViewModel,
    onManageTasks: () -> Unit
) {
    val taskList by taskViewModel.tasks.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Selects and shows random task
        TaskSlotMachine(tasks = taskList)

        OutlinedButton(
            onClick = onManageTasks,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Manage tasks")
        }
    }
}