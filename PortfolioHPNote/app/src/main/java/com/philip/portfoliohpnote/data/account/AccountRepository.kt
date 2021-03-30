package com.philip.portfoliohpnote.data.account

import android.content.Context
import android.util.Log
import com.philip.portfoliohpnote.data.account.local.EncryptedAccountLocalDataSource
import org.json.JSONObject


class AccountRepository(context: Context) : AccountDataSource {

    private val accountLocalDataSource = EncryptedAccountLocalDataSource(context)

    override fun autoLoginCheck(callback: AccountDataSource.LoginResultCallback) {
        accountLocalDataSource.autoLoginCheck(callback)
    }

    override fun saveLoginAccount(account: JSONObject, callback: AccountDataSource.LoginResultCallback) {
        accountLocalDataSource.saveLoginAccount(account, callback)
    }

    override fun setLogout() {
        accountLocalDataSource.setLogout()
    }

    override fun saveSignUpAccount(userEmail: String, account: JSONObject) {
        accountLocalDataSource.saveSignUpAccount(userEmail, account)
    }

    override fun getSignUpAccount(userEmail: String, callback: AccountDataSource.LoadAccountCallback) {
        accountLocalDataSource.getSignUpAccount(userEmail, callback)
    }

    override fun getLoginedUserInfo(callback: AccountDataSource.LoadAccountCallback) {
        accountLocalDataSource.getLoginedUserInfo(callback)
    }
}