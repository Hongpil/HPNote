package com.philip.portfoliohpnote.data.tasks.source

import android.content.Context
import com.philip.portfoliohpnote.data.tasks.source.local.TasksLocalDataSource
import com.philip.portfoliohpnote.data.tasks.source.local.ToDoDatabase

object Injection {

    fun provideTasksRepository(context: Context): TasksRepository {

        val database = ToDoDatabase.getInstance(context)

        return TasksRepository.getInstance(
            TasksLocalDataSource.getInstance(database.taskDao())
        )
    }
}
