package com.philip.portfoliohpnote.view.tasks.presenter

import com.philip.portfoliohpnote.BasePresenter
import com.philip.portfoliohpnote.data.account.AccountDataSource
import com.philip.portfoliohpnote.data.tasks.Task
import com.philip.portfoliohpnote.view.tasks.TasksFilterType

interface TasksContract {

    interface View {

        var isActive: Boolean

        fun setLoadingIndicator(active: Boolean)

        fun showTasks(tasks: List<Task>)

        fun showAddTask()

        fun showTaskDetailsUi(taskId: String)

        fun showTaskMarkedComplete()

        fun showTaskMarkedActive()

        fun showCompletedTasksCleared()

        fun showLoadingTasksError()

        fun showNoTasks()

        fun showActiveFilterLabel()

        fun showCompletedFilterLabel()

        fun showAllFilterLabel()

        fun showNoActiveTasks()

        fun showNoCompletedTasks()

        fun showSuccessfullySavedMessage()

        fun showFilteringPopUpMenu()
    }

    interface Presenter : BasePresenter {

        var currentFiltering: TasksFilterType

        fun result(requestCode: Int, resultCode: Int)

        fun loadTasks(userEmail: String, forceUpdate: Boolean)

        fun addNewTask()

        fun openTaskDetails(requestedTask: Task)

        fun completeTask(userEmail: String, completedTask: Task)

        fun activateTask(userEmail: String, activeTask: Task)

        fun clearCompletedTasks(userEmail: String)

        fun getLoginUserInfo(callback: AccountDataSource.LoadAccountCallback)
    }
}