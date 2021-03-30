package com.philip.portfoliohpnote.view.signup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.philip.portfoliohpnote.R
import com.philip.portfoliohpnote.data.account.AccountRepository
import com.philip.portfoliohpnote.util.showSnackBar
import com.philip.portfoliohpnote.view.signup.presenter.SignUpContract
import com.philip.portfoliohpnote.view.signup.presenter.SignUpPresenter
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class SignUpActivity : AppCompatActivity(), View.OnClickListener, SignUpContract.View {

    private val presenter: SignUpPresenter by lazy {
        SignUpPresenter(this@SignUpActivity, AccountRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_store -> {
                if (edit_email.length() == 0) {
                    Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show()
                    return
                }
                if (edit_nickname.length() == 0) {
                    Toast.makeText(this, "Please enter your nickname.", Toast.LENGTH_SHORT).show()
                    return
                }
                if (edit_password.length() == 0) {
                    Toast.makeText(this, "Please enter your password.", Toast.LENGTH_SHORT).show()
                    return
                }
                if (edit_password_confirm.length() == 0) {
                    Toast.makeText(this, "Please enter your password again.", Toast.LENGTH_SHORT).show()
                    return
                }
                if (edit_password.text.toString() != edit_password_confirm.text.toString()) {
                    Toast.makeText(this, "Password do not match.\nPlease enter again.", Toast.LENGTH_SHORT).show()
                    edit_password_confirm.text.clear()
                    edit_password_confirm.requestFocus()
                    return
                }

                var userSex: String? = null
                if (!rg_btn1.isChecked && !rg_btn2.isChecked) {
                    Toast.makeText(this, "Please select your gender.", Toast.LENGTH_SHORT).show()
                    return
                } else {
                    if (rg_btn1.isChecked) {
                        userSex = "male"
                    } else if (rg_btn2.isChecked) {
                        userSex = "female"
                    }
                }

                val account = JSONObject()
                account.put("user_email", edit_email.text.toString())
                account.put("user_nickname", edit_nickname.text.toString())
                account.put("user_password", edit_password.text.toString())
                account.put("user_sex", userSex)

                presenter.saveSignUpAccount(edit_email.text.toString(), account)
            }
        }
    }

    override fun showSuccessfullySavedMessage(account: JSONObject) {

        showMessage("회원가입 성공 !")

        // SnackBar 를 화면에 보여주기 위해, 1초 후 로그인 화면으로 전환
        Timer().schedule(1000) {
            val intent = Intent()
            intent.putExtra("userEmail", account.getString("user_email"))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun showMessage(message: String) {
        signup_layout.showSnackBar(message, Snackbar.LENGTH_SHORT)
    }

    companion object {
        const val REQUEST_ADD_TASK = 1
    }
}