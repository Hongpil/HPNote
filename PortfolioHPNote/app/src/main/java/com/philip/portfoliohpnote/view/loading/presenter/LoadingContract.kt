package com.philip.portfoliohpnote.view.loading.presenter

import org.json.JSONObject

interface LoadingContract {

    // onCreate 화면 초기화 시, 저장된 데이터가 있는지 Model 에서 확인하고 결과에 따라 로그인화면으로 전환할 지 메인화면으로 전환할 지 지시한다.
    interface View {
        fun loadingSuccess(account: JSONObject)
        fun loadingFail()
    }

    interface Presenter {
        fun autoLoginCheck()
    }
}