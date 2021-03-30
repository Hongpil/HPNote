package com.philip.portfoliohpnote.view.tasks.presenter

import android.app.Activity
import com.philip.portfoliohpnote.data.account.AccountDataSource
import com.philip.portfoliohpnote.data.account.AccountRepository
import com.philip.portfoliohpnote.data.tasks.Task
import com.philip.portfoliohpnote.data.tasks.source.TasksDataSource
import com.philip.portfoliohpnote.data.tasks.source.TasksRepository
import com.philip.portfoliohpnote.view.taskadd.AddEditTaskActivity
import com.philip.portfoliohpnote.view.tasks.TasksFilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class TasksPresenter(
    private val accountRepository: AccountRepository,
    val tasksRepository: TasksRepository,
    val tasksView: TasksContract.View)
    : TasksContract.Presenter {

    override var currentFiltering = TasksFilterType.ALL_TASKS

    private var firstLoad = true

    override fun start() {

    }

    override fun result(requestCode: Int, resultCode: Int) {
        // If a task was successfully added, show snackbar
        if (AddEditTaskActivity.REQUEST_ADD_TASK ==
            requestCode && Activity.RESULT_OK == resultCode) {
            tasksView.showSuccessfullySavedMessage()
        }
    }

    override fun loadTasks(userEmail: String, forceUpdate: Boolean) {
        // Simplification for sample: a network reload will be forced on first load.
        loadTasks(userEmail, forceUpdate || firstLoad, true)
        firstLoad = false
    }

    private fun loadTasks(userEmail: String, forceUpdate: Boolean, showLoadingUI: Boolean) {

//        if (showLoadingUI) {
//            tasksView.setLoadingIndicator(true)
//        }

        tasksRepository.getTasks(userEmail, object : TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                val tasksToShow = ArrayList<Task>()

                // We filter the tasks based on the requestType
                for (task in tasks) {
                    when (currentFiltering) {
                        TasksFilterType.ALL_TASKS -> tasksToShow.add(task)
                        TasksFilterType.ACTIVE_TASKS -> if (task.isActive) {
                            tasksToShow.add(task)
                        }
                        TasksFilterType.COMPLETED_TASKS -> if (task.isCompleted) {
                            tasksToShow.add(task)
                        }
                    }
                }
                // The view may not be able to handle UI updates anymore
                if (!tasksView.isActive) {
                    return
                }
                if (showLoadingUI) {
                    tasksView.setLoadingIndicator(false)
                }

                processTasks(tasksToShow)
            }

            override fun onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!tasksView.isActive) {
                    return
                }
                //tasksView.showLoadingTasksError()
            }
        })
    }

    private fun processTasks(tasks: List<Task>) {
        if (tasks.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            processEmptyTasks()
        } else {
            // Show the list of tasks
            // Coroutine 사용
            // Coroutine 사용 안 하면, the original thread that created a view hierarchy can touch its views. coroutine 에러 발생함.
            GlobalScope.launch(Dispatchers.Main) {
                tasksView.showTasks(tasks)
            }
            // Set the filter label's text.
            showFilterLabel()
        }
    }

    private fun showFilterLabel() {
        when (currentFiltering) {
            TasksFilterType.ACTIVE_TASKS -> tasksView.showActiveFilterLabel()
            TasksFilterType.COMPLETED_TASKS -> tasksView.showCompletedFilterLabel()
            else -> tasksView.showAllFilterLabel()
        }
    }

    private fun processEmptyTasks() {
        when (currentFiltering) {
            TasksFilterType.ACTIVE_TASKS -> tasksView.showNoActiveTasks()
            TasksFilterType.COMPLETED_TASKS -> tasksView.showNoCompletedTasks()
            else -> tasksView.showNoTasks()
        }
    }

    override fun addNewTask() {
        tasksView.showAddTask()
    }

    override fun openTaskDetails(requestedTask: Task) {
        tasksView.showTaskDetailsUi(requestedTask.id)
    }

    override fun completeTask(userEmail: String, completedTask: Task) {
        tasksRepository.completeTask(completedTask)
        tasksView.showTaskMarkedComplete()
        loadTasks(userEmail, false, false)
    }

    override fun activateTask(userEmail: String, activeTask: Task) {
        tasksRepository.activateTask(activeTask)
        tasksView.showTaskMarkedActive()
        loadTasks(userEmail, false, false)
    }

    override fun clearCompletedTasks(userEmail: String) {
        tasksRepository.clearCompletedTasks()
        tasksView.showCompletedTasksCleared()
        loadTasks(userEmail, false, false)
    }

    override fun getLoginUserInfo(callback: AccountDataSource.LoadAccountCallback) {
        accountRepository.getLoginedUserInfo(callback)
    }

}