package com.philip.portfoliohpnote.view.taskdetail.presenter

import com.philip.portfoliohpnote.data.tasks.Task
import com.philip.portfoliohpnote.data.tasks.source.TasksDataSource
import com.philip.portfoliohpnote.data.tasks.source.TasksRepository

class TaskDetailPresenter(
    private val taskId: String,
    private val tasksRepository: TasksRepository,
    private val taskDetailView: TaskDetailContract.View
) : TaskDetailContract.Presenter {

    init {
        taskDetailView.presenter = this
    }

    override fun start() {
        openTask()
    }

    private fun openTask() {
        if (taskId.isEmpty()) {
            taskDetailView.showMissingTask()
            return
        }

        taskDetailView.setLoadingIndicator(true)
        tasksRepository.getTask(taskId, object : TasksDataSource.GetTaskCallback {
            override fun onTaskLoaded(task: Task) {
                with(taskDetailView) {
                    // The view may not be able to handle UI updates anymore
                    if (!isActive) {
                        return@onTaskLoaded
                    }
                    setLoadingIndicator(false)
                }
                showTask(task)
            }

            override fun onDataNotAvailable() {
                with(taskDetailView) {
                    // The view may not be able to handle UI updates anymore
                    if (!isActive) {
                        return@onDataNotAvailable
                    }
                    showMissingTask()
                }
            }
        })
    }

    override fun editTask() {
        if (taskId.isEmpty()) {
            taskDetailView.showMissingTask()
            return
        }
        taskDetailView.showEditTask(taskId)
    }

    override fun deleteTask() {
        if (taskId.isEmpty()) {
            taskDetailView.showMissingTask()
            return
        }
        tasksRepository.deleteTask(taskId)
        taskDetailView.showTaskDeleted()
    }

    override fun completeTask() {
        if (taskId.isEmpty()) {
            taskDetailView.showMissingTask()
            return
        }
//        tasksRepository.completeTask(taskId)
        taskDetailView.showTaskMarkedComplete()
    }

    override fun activateTask() {
        if (taskId.isEmpty()) {
            taskDetailView.showMissingTask()
            return
        }
//        tasksRepository.activateTask(taskId)
        taskDetailView.showTaskMarkedActive()
    }

    private fun showTask(task: Task) {
        with(taskDetailView) {
            if (taskId.isEmpty()) {
                hideTitle()
                hideDescription()
            } else {
                showTitle(task.title)
                showDescription(task.description)
            }
            showCompletionStatus(task.isCompleted)
        }
    }
}