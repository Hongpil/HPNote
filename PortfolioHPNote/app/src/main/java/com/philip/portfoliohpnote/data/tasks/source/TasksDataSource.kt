package com.philip.portfoliohpnote.data.tasks.source

import com.philip.portfoliohpnote.data.tasks.Task

interface TasksDataSource {

    interface LoadTasksCallback {

        fun onTasksLoaded(tasks: List<Task>)

        fun onDataNotAvailable()
    }

    interface GetTaskCallback {

        fun onTaskLoaded(task: Task)

        fun onDataNotAvailable()
    }

    fun getTasks(userEmail: String, callback: LoadTasksCallback)

    fun getTask(taskId: String, callback: GetTaskCallback)

    fun saveTask(task: Task)

    fun completeTask(task: Task)

    fun activateTask(task: Task)

    fun clearCompletedTasks()

    fun deleteAllTasks()

    fun deleteTask(taskId: String)
}