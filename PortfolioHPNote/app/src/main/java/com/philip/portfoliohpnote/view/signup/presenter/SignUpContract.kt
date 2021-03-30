package com.philip.portfoliohpnote.view.signup.presenter

import org.json.JSONObject

interface SignUpContract {

    interface View {
        fun showSuccessfullySavedMessage(account: JSONObject)
    }

    interface Presenter {
        fun saveSignUpAccount(userEmail: String, account: JSONObject)
    }
}