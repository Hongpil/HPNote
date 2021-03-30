package com.philip.portfoliohpnote.view.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.philip.portfoliohpnote.R
import com.philip.portfoliohpnote.data.account.AccountRepository
import com.philip.portfoliohpnote.util.showSnackBar
import com.philip.portfoliohpnote.view.MainActivity
import com.philip.portfoliohpnote.view.login.presenter.LoginContract
import com.philip.portfoliohpnote.view.login.presenter.LoginPresenter
import com.philip.portfoliohpnote.view.signup.SignUpActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class LoginActivity : AppCompatActivity(), LoginContract.View {

    private val presenter: LoginPresenter by lazy {
        LoginPresenter(this@LoginActivity, AccountRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initButtonListener()
    }

    private fun initButtonListener() {
        btn_login.setOnClickListener {
            val account = JSONObject()
            account.put("user_email", edt_email.text.toString())
            account.put("user_password", edt_password.text.toString())
            presenter.setLogin(account)
        }

        btn_signup.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivityForResult(intent, SignUpActivity.REQUEST_ADD_TASK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SignUpActivity.REQUEST_ADD_TASK -> {
                    edt_email.setText(data!!.getStringExtra("userEmail").toString())
                }
            }
        }
    }

    override fun loginSuccess(account: JSONObject) {

        showMessage("로그인 성공!")

        // SnackBar 를 화면에 보여주기 위해, 1초 후 메인 화면으로 전환
        Timer().schedule(1000) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.putExtra("user_email", account.getString("user_email"))
            intent.putExtra("user_nickname", account.getString("user_nickname"))
            intent.putExtra("user_password", account.getString("user_password"))
            intent.putExtra("user_sex", account.getString("user_sex"))
            startActivity(intent)
            finish()
        }
    }

    override fun loginFail() {
        edt_password.text.clear()
        showMessage("로그인 실패!")
    }

    private fun showMessage(message: String) {
        login_layout.showSnackBar(message, Snackbar.LENGTH_SHORT)
    }
}