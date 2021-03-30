package com.philip.portfoliohpnote.view.loading

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.philip.portfoliohpnote.R
import com.philip.portfoliohpnote.data.account.AccountRepository
import com.philip.portfoliohpnote.view.MainActivity
import com.philip.portfoliohpnote.view.loading.presenter.LoadingContract
import com.philip.portfoliohpnote.view.loading.presenter.LoadingPresenter
import com.philip.portfoliohpnote.view.login.LoginActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*
import org.json.JSONObject

class SplashScreenActivity : AppCompatActivity(), LoadingContract.View {

    private val presenter: LoadingPresenter by lazy {
        LoadingPresenter(this@SplashScreenActivity, AccountRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        presenter.autoLoginCheck()
    }

    override fun loadingSuccess(account: JSONObject) {

        iv_note.alpha = 0f
        iv_note.animate().setDuration(1500).alpha(1f).withEndAction {

            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            intent.putExtra("user_email", account.getString("user_email"))
            intent.putExtra("user_nickname", account.getString("user_nickname"))
            intent.putExtra("user_password", account.getString("user_password"))
            intent.putExtra("user_sex", account.getString("user_sex"))
            startActivity(intent)

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    override fun loadingFail() {

        iv_note.alpha = 0f
        iv_note.animate().setDuration(1500).alpha(1f).withEndAction {

            val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}