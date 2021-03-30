package com.philip.portfoliohpnote.data.account

import org.json.JSONObject

interface AccountDataSource {

    interface LoadAccountCallback {
        fun onAccountLoaded(account: JSONObject)
        fun onDataNotAvailable()
    }

    interface LoginResultCallback {
        fun onLoginSuccess(account: JSONObject)
        fun onLoginFail()
    }

    fun autoLoginCheck(callback: LoginResultCallback)
    fun saveLoginAccount(account: JSONObject, callback: LoginResultCallback)
    fun setLogout()

    fun saveSignUpAccount(userEmail: String, account: JSONObject)
    fun getSignUpAccount(userEmail: String, callback: LoadAccountCallback)

    fun getLoginedUserInfo(callback: LoadAccountCallback)
}