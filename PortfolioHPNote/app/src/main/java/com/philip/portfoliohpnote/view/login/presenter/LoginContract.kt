package com.philip.portfoliohpnote.view.login.presenter

import org.json.JSONObject

interface LoginContract {

    interface View {
        fun loginSuccess(account: JSONObject)
        fun loginFail()
    }

    interface Presenter {
        // TextView 에 account 데이터를 보여주라고 View 에게 지시한다
        fun setLogin(account: JSONObject)
    }
}