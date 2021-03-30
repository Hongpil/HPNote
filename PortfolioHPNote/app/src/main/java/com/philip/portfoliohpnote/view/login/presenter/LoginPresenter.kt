package com.philip.portfoliohpnote.view.login.presenter

import com.philip.portfoliohpnote.data.account.AccountDataSource
import com.philip.portfoliohpnote.data.account.AccountRepository
import org.json.JSONObject

class LoginPresenter(
    private val view: LoginContract.View,
    private val repository: AccountRepository
) : LoginContract.Presenter {

    override fun setLogin(account: JSONObject) {
        repository.saveLoginAccount(account, object : AccountDataSource.LoginResultCallback {
            override fun onLoginSuccess(account: JSONObject) {
                view.loginSuccess(account)
            }

            override fun onLoginFail() {
                view.loginFail()
            }

        })
    }
}