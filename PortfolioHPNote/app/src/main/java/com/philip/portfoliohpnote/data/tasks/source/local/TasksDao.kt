package com.philip.portfoliohpnote.data.tasks.source.local

import androidx.room.*
import com.philip.portfoliohpnote.data.tasks.Task

/**
 * Data Access Object for the tasks table.
 */
@Dao
interface TasksDao {

    /**
     * Select a task by email.
     */
    @Query("SELECT * FROM Tasks WHERE email = :userEmail") fun getTasks(userEmail: String): List<Task>

    /**
     * Select a task by id.
     */
    @Query("SELECT * FROM Tasks WHERE entryid = :taskId") fun getTaskById(taskId: String): Task?

    /**
     * Insert a task in the database. If the task already exists, replace it.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertTask(task: Task)

    /**
     * Update a task.
     */
    @Update
    fun updateTask(task: Task): Int

    /**
     * Update the complete status of a task
     */
    @Query("UPDATE tasks SET completed = :completed WHERE entryid = :taskId")
    fun updateCompleted(taskId: String, completed: Boolean)

    /**
     * Delete a task by id.
     */
    @Query("DELETE FROM Tasks WHERE entryid = :taskId") fun deleteTaskById(taskId: String): Int

    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM Tasks") fun deleteTasks()

    /**
     * Delete all completed tasks from the table.
     */
    @Query("DELETE FROM Tasks WHERE completed = 1") fun deleteCompletedTasks(): Int
}