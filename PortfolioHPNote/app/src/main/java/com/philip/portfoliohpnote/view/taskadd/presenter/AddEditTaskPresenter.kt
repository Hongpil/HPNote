package com.philip.portfoliohpnote.view.taskadd.presenter

import com.philip.portfoliohpnote.data.account.AccountDataSource
import com.philip.portfoliohpnote.data.account.AccountRepository
import com.philip.portfoliohpnote.data.tasks.Task
import com.philip.portfoliohpnote.data.tasks.source.TasksDataSource


class AddEditTaskPresenter(
    private val taskId: String?,
    private val accountRepository: AccountRepository,
    private val tasksRepository: TasksDataSource,
    private val addTaskView: AddEditTaskContract.View,
    override var isDataMissing: Boolean
) : AddEditTaskContract.Presenter, TasksDataSource.GetTaskCallback {

    init {
        addTaskView.presenter = this
    }

    override fun start() {
        if (taskId != null && isDataMissing) {
            populateTask()
        }
    }

    override fun saveTask(email: String, title: String, description: String) {
        if (taskId == null) {
            createTask(email, title, description)
        } else {
            updateTask(email, title, description)
        }
    }

    override fun populateTask() {
        if (taskId == null) {
            throw RuntimeException("populateTask() was called but task is new.")
        }
        tasksRepository.getTask(taskId, this)
    }

    override fun getLoginUserInfo(callback: AccountDataSource.LoadAccountCallback) {
        accountRepository.getLoginedUserInfo(callback)
    }

    override fun onTaskLoaded(task: Task) {
        // The view may not be able to handle UI updates anymore
        if (addTaskView.isActive) {
            addTaskView.setTitle(task.title)
            addTaskView.setDescription(task.description)
        }
        isDataMissing = false
    }

    override fun onDataNotAvailable() {
        // The view may not be able to handle UI updates anymore
        if (addTaskView.isActive) {
            addTaskView.showEmptyTaskError()
        }
    }

    private fun createTask(email: String, title: String, description: String) {
        val newTask = Task(email, title, description)
        if (newTask.isEmpty) {
            addTaskView.showEmptyTaskError()
        } else {
            tasksRepository.saveTask(newTask)
            addTaskView.showTasksList()
        }
    }

    private fun updateTask(email: String, title: String, description: String) {
        if (taskId == null) {
            throw RuntimeException("updateTask() was called but task is new.")
        }
        tasksRepository.saveTask(Task(email, title, description, taskId))
        addTaskView.showTasksList() // After an edit, go back to the list.
    }
}