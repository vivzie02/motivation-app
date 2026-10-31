package com.example.motivationapp.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao
) {

    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
    }

    suspend fun addTask(name: String) {
        val cleanedName = name.trim()

        if (cleanedName.isNotBlank()) {
            taskDao.insertTask(Task(name = cleanedName))
        }
    }

    suspend fun removeTask(task: Task) {
        taskDao.deleteTask(task)
    }
}