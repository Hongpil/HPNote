package com.philip.portfoliohpnote.data.tasks.source

import com.philip.portfoliohpnote.data.tasks.Task

class TasksRepository(
    val tasksLocalDataSource: TasksDataSource
) : TasksDataSource {

    override fun getTasks(userEmail: String, callback: TasksDataSource.LoadTasksCallback) {
        getTasksFromLocalDataSource(userEmail, callback)
    }

    override fun saveTask(task: Task) {
        tasksLocalDataSource.saveTask(task)
    }

    override fun completeTask(task: Task) {
        tasksLocalDataSource.completeTask(task)
    }

    override fun activateTask(task: Task) {
        tasksLocalDataSource.activateTask(task)
    }

    override fun clearCompletedTasks() {
        tasksLocalDataSource.clearCompletedTasks()
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {

        tasksLocalDataSource.getTask(taskId, object : TasksDataSource.GetTaskCallback {
            override fun onTaskLoaded(task: Task) {
                callback.onTaskLoaded(task)
            }
            override fun onDataNotAvailable() {
                //
            }
        })
    }

    override fun deleteAllTasks() {
        tasksLocalDataSource.deleteAllTasks()
    }

    override fun deleteTask(taskId: String) {
        tasksLocalDataSource.deleteTask(taskId)
    }

    private fun getTasksFromLocalDataSource(userEmail: String, callback: TasksDataSource.LoadTasksCallback) {
        tasksLocalDataSource.getTasks(userEmail, object : TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                callback.onTasksLoaded(ArrayList(tasks))
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    companion object {

        private var INSTANCE: TasksRepository? = null

        @JvmStatic fun getInstance(
            tasksLocalDataSource: TasksDataSource
        ): TasksRepository {

            return INSTANCE ?: TasksRepository(tasksLocalDataSource)
                .apply { INSTANCE = this }
        }
        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }
}