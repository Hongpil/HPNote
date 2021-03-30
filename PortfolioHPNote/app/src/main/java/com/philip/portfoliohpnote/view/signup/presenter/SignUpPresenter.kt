package com.philip.portfoliohpnote.view.signup.presenter

import android.util.Log
import com.philip.portfoliohpnote.data.account.AccountDataSource
import com.philip.portfoliohpnote.data.account.AccountRepository
import org.json.JSONObject

class SignUpPresenter(
        private val view: SignUpContract.View,
        private val repository: AccountRepository
) : SignUpContract.Presenter {

    override fun saveSignUpAccount(userEmail: String, account: JSONObject) {

        repository.saveSignUpAccount(userEmail, account)

        repository.getSignUpAccount(userEmail, object : AccountDataSource.LoadAccountCallback {
            override fun onAccountLoaded(account: JSONObject) {
                view.showSuccessfullySavedMessage(account)
            }

            override fun onDataNotAvailable() {
                //
            }

        })
    }
}