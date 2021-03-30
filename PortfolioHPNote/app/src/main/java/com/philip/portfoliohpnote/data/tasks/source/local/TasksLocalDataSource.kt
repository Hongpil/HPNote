package com.philip.portfoliohpnote.data.tasks.source.local

import androidx.annotation.VisibleForTesting
import com.philip.portfoliohpnote.data.tasks.Task
import com.philip.portfoliohpnote.data.tasks.source.TasksDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Concrete implementation of a data source as a db.
 */
class TasksLocalDataSource private constructor(
    val tasksDao: TasksDao
) : TasksDataSource {

    /**
     * Note: [TasksDataSource.LoadTasksCallback.onDataNotAvailable] is fired if the database doesn't exist
     * or the table is empty.
     */
    override fun getTasks(userEmail: String, callback: TasksDataSource.LoadTasksCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val tasks = tasksDao.getTasks(userEmail)
            if (tasks.isEmpty()) {
                // This will be called if the table is new or just empty.
                callback.onDataNotAvailable()
            } else {
                callback.onTasksLoaded(tasks)
            }
        }
    }

    /**
     * Note: [TasksDataSource.GetTaskCallback.onDataNotAvailable] is fired if the [Task] isn't
     * found.
     */
    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val task = tasksDao.getTaskById(taskId)
            if (task != null) {
                callback.onTaskLoaded(task)
            } else {
                callback.onDataNotAvailable()
            }
        }
    }

    override fun saveTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            tasksDao.insertTask(task)
        }
    }

    override fun completeTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            tasksDao.updateCompleted(task.id, true)
        }
    }

    override fun activateTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            tasksDao.updateCompleted(task.id, false)
        }
    }

    override fun clearCompletedTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            tasksDao.deleteCompletedTasks()
        }
    }

    override fun deleteAllTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            tasksDao.deleteTasks()
        }
    }

    override fun deleteTask(taskId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            tasksDao.deleteTaskById(taskId)
        }
    }

    companion object {
        private var INSTANCE: TasksLocalDataSource? = null

        @JvmStatic
        fun getInstance(tasksDao: TasksDao): TasksLocalDataSource {
            if (INSTANCE == null) {
                synchronized(TasksLocalDataSource::javaClass) {
                    INSTANCE = TasksLocalDataSource(tasksDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}