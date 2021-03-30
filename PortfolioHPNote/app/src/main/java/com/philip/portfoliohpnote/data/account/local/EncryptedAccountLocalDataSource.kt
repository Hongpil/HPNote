package com.philip.portfoliohpnote.data.account.local

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.philip.portfoliohpnote.data.account.AccountDataSource
import org.json.JSONArray
import org.json.JSONObject

class EncryptedAccountLocalDataSource(context: Context) : AccountDataSource {

    companion object{
        private const val TAG = "SharedUtil"
        private const val MASTER_KEY_ALIAS = "_androidx_security_master_key_"
        private const val KEY_SIZE = 256
        private const val PREFERENCE_FILE_KEY = "_preferences"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        val spec = KeyGenParameterSpec
            .Builder(MASTER_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE)
            .build()

        val masterKey = MasterKey
            .Builder(context)
            .setKeyGenParameterSpec(spec)
            .build()

        EncryptedSharedPreferences.create(
            context,
            context.packageName + PREFERENCE_FILE_KEY,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun autoLoginCheck(callback: AccountDataSource.LoginResultCallback) {
        var loginedUserInfo: JSONObject? = null
        val getLoginSharedPref = sharedPreferences.getString("login_UserAccount", null)
        getLoginSharedPref?.let {
            // null 이 아닌 경우
            loginedUserInfo = JSONObject(getLoginSharedPref)
        } ?: {
            // null 인 경우
            loginedUserInfo = JSONObject()
        } ()

        loginedUserInfo?.let {
            // JSONObject 에서 해당 key 를 가지고 있는지 검사
            if (loginedUserInfo?.has("user_autoLogin") == true) {
                // "user_autoLogin" key 가 true 인지 검사
                if (loginedUserInfo!!.get("user_autoLogin") == true) {
                    callback.onLoginSuccess(it)
                } else {
                    callback.onLoginFail()
                }
            } else {
                callback.onLoginFail()
            }
        } ?: {
            callback.onLoginFail()
        } ()
    }

    override fun saveLoginAccount(account: JSONObject, callback: AccountDataSource.LoginResultCallback) {
        // loginSharedPreferences 에 저장할 JSONObject 객체 - JSONObject 객체에는 이메일,닉네임,비밀번호,성별,자동로그인유무 데이터가 저장된다.
        val loginUserJSONObject = JSONObject()
        var isAutoLogin = false
        var jsonUserEmail: String? = null
        var jsonUserNickname: String? = null
        var jsonUserPassword: String? = null
        var jsonUserSex: String? = null

        var usersJSONObject: JSONObject? = null
        val getSignupSharedPref = sharedPreferences.getString(account.get("user_email").toString(), null)
        getSignupSharedPref?.let {
            // null 이 아닌 경우
            usersJSONObject = JSONObject(getSignupSharedPref)
        } ?: {
            // null 인 경우
            usersJSONObject = JSONObject()
        } ()

        // JSONObject 에서 해당 key 를 가지고 있는지 검사
        if (usersJSONObject?.has("user_email") == true &&
            usersJSONObject?.has("user_nickname") == true &&
            usersJSONObject?.has("user_password") == true &&
            usersJSONObject?.has("user_sex") == true) {

            jsonUserEmail = usersJSONObject?.getString("user_email")
            jsonUserNickname = usersJSONObject?.getString("user_nickname")
            jsonUserPassword = usersJSONObject?.getString("user_password")
            jsonUserSex = usersJSONObject?.getString("user_sex")

            if (account.get("user_email").toString() == jsonUserEmail) {
                if (account.get("user_password").toString() == jsonUserPassword) {
                    // 로그인 성공
                    isAutoLogin = true

                    loginUserJSONObject.put("user_email", jsonUserEmail)
                    loginUserJSONObject.put("user_nickname", jsonUserNickname)
                    loginUserJSONObject.put("user_password", jsonUserPassword)
                    loginUserJSONObject.put("user_sex", jsonUserSex)
                    loginUserJSONObject.put("user_autoLogin", isAutoLogin)

                    putAny("login_UserAccount", loginUserJSONObject)
                }
            }
        }

        if (isAutoLogin) {  // 로그인 성공
            callback.onLoginSuccess(loginUserJSONObject)
        } else {    // 로그인 실패
            callback.onLoginFail()
        }
    }

    override fun setLogout() {
        val logoutJSONObject = JSONObject()
        logoutJSONObject.put("user_email", null)
        logoutJSONObject.put("user_nickname", null)
        logoutJSONObject.put("user_password", null)
        logoutJSONObject.put("user_sex", null)
        logoutJSONObject.put("user_autoLogin", false)

        putAny("login_UserAccount", logoutJSONObject)
    }

    override fun saveSignUpAccount(userEmail: String, account: JSONObject) {

        var usersJSONArray: JSONArray? = null
        val getSignupSharedPref = sharedPreferences.getString("users_JSONArray", null)
        getSignupSharedPref?.let {
            // null 이 아닌 경우
            usersJSONArray = JSONArray(getSignupSharedPref)
        } ?: {
            // null 인 경우
            usersJSONArray = JSONArray()
        } ()

        /**
         * 입력받은 이메일이 기존에 가입되어 있는 것인지 검사한다.
         */
        for (i in 0 until usersJSONArray?.length()!!) {
            val getJSONObject: JSONObject = usersJSONArray!!.getJSONObject(i)
            val getUserEmail = getJSONObject.getString("user_email")
            val getUserNickname = getJSONObject.getString("user_nickname")
            val getUserPassword = getJSONObject.getString("user_password")
            val getUserSex = getJSONObject.getString("user_sex")

            // 가입되어 있는 email 중복 입력시 -> 기존 email 의 JSONObject 삭제. (이후 새로 입력받은 email 의 JSONObject 저장)
            if (userEmail == getUserEmail) {
                usersJSONArray!!.remove(i)
                break
            }
        }

        val usersJSONObject = JSONObject()
        usersJSONObject.put("user_email", userEmail)
        usersJSONObject.put("user_nickname", account.get("user_nickname"))
        usersJSONObject.put("user_password", account.get("user_password"))
        usersJSONObject.put("user_sex", account.get("user_sex"))
        usersJSONArray?.put(usersJSONObject)

        putAny(userEmail, account)
        putAny("users_JSONArray", usersJSONArray!!)

    }

    override fun getSignUpAccount(userEmail: String, callback: AccountDataSource.LoadAccountCallback) {

        val getShared: String? = sharedPreferences.getString(userEmail, null)
        getShared?.let {
            // null 이 아닌 경우
            callback.onAccountLoaded(JSONObject(getShared))
        } ?: {
            // null 인 경우
            callback.onDataNotAvailable()
        } ()
    }

    override fun getLoginedUserInfo(callback: AccountDataSource.LoadAccountCallback) {

        val getShared: String? = sharedPreferences.getString("login_UserAccount", null)
        getShared?.let {
            // null 이 아닌 경우
            callback.onAccountLoaded(JSONObject(getShared))
        } ?: {
            // null 인 경우
            callback.onDataNotAvailable()
        } ()
    }


    private fun putAny(key: String, value: Any){
        val editor = sharedPreferences.edit()

        with(editor) {
            when (value) {
                is Int -> {
                    Log.d(TAG, "Input Key is $key, Int Value is $value ")
                    putInt(key, value)
                }
                is String -> {
                    Log.d(TAG, "Input Key is $key, String Value is $value ")
                    putString(key, value)
                }
                is Boolean -> {
                    Log.d(TAG, "Input Key is $key, Boolean Value is $value ")
                    putBoolean(key, value)
                }
                is JSONObject -> {
                    Log.d(TAG, "Input Key is $key, JSONObject Value is $value")
                    putString(key, value.toString())
                }
                is JSONArray -> {
                    Log.d(TAG, "Input Key is #$key, JSONArray Value is $value")
                    putString(key, value.toString())
                }
                else -> throw IllegalArgumentException("잘못된 인자입니다.")
            }
            apply()
        }
    }

    fun getString(key: String) = sharedPreferences.getString(key, "")

    fun getNumber(key: String) = sharedPreferences.getInt(key, 0)

    fun getBoolean(key: String) = sharedPreferences.getBoolean(key, false)
}