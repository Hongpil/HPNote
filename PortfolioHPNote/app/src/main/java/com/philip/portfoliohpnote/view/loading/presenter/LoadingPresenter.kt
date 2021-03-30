package com.philip.portfoliohpnote.view.loading.presenter

import com.philip.portfoliohpnote.data.account.AccountDataSource
import com.philip.portfoliohpnote.data.account.AccountRepository
import org.json.JSONObject

class LoadingPresenter(
    private val view: LoadingContract.View,
    private val repository: AccountRepository
) : LoadingContract.Presenter {

    override fun autoLoginCheck() {
        repository.autoLoginCheck(object : AccountDataSource.LoginResultCallback {
            override fun onLoginSuccess(account: JSONObject) {
                view.loadingSuccess(account)
            }

            override fun onLoginFail() {
                view.loadingFail()
            }
        })
    }
}