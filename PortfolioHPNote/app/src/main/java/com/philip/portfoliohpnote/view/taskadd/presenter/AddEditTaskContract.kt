package com.philip.portfoliohpnote.view.taskadd.presenter

import com.philip.portfoliohpnote.BasePresenter
import com.philip.portfoliohpnote.BaseView
import com.philip.portfoliohpnote.data.account.AccountDataSource

/**
 * This specifies the contract between the view and the presenter.
 */
interface AddEditTaskContract {

    interface View : BaseView<Presenter> {
        var isActive: Boolean

        fun showEmptyTaskError()

        fun showTasksList()

        fun setTitle(title: String)

        fun setDescription(description: String)

    }

    interface Presenter : BasePresenter {
        var isDataMissing: Boolean

        fun saveTask(email: String, title: String, description: String)

        fun populateTask()

        fun getLoginUserInfo(callback: AccountDataSource.LoadAccountCallback)
    }
}