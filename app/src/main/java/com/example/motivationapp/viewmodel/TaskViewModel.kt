package com.example.motivationapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motivationapp.data.Task
import com.example.motivationapp.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    val tasks = repository.getAllTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTask(name: String) {
        viewModelScope.launch {
            repository.addTask(name)
        }
    }

    fun removeTask(task: Task) {
        viewModelScope.launch {
            repository.removeTask(task)
        }
    }
}