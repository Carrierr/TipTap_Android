package me.tiptap.tiptap.common.util.login

import com.kakao.auth.ISessionCallback
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.ApiErrorCode
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import com.kakao.util.helper.log.Logger
import me.tiptap.tiptap.R
import me.tiptap.tiptap.TipTapApplication
import me.tiptap.tiptap.data.User

class KaKaoSessionCallback(val navigator: LoginNavigator) : ISessionCallback {

    private var type: String = ""

    init {
        TipTapApplication.getTipTapApplicationContext()?.let {
            type = it.getString(R.string.type_kakao)
        }
    }

    override fun onSessionOpenFailed(exception: KakaoException?) {
        if (exception != null) Logger.e(exception)
    }

    override fun onSessionOpened() {
        requestMe()
    }


    private fun requestMe() {
        UserManagement.getInstance().me(object : MeV2ResponseCallback() {
            override fun onSuccess(result: MeV2Response?) {
                //result 가 null 이 아니면 유저의 정보 담아서 보냄.
                result?.let {
                    navigator.getAccessToken(
                            User(type,
                                    it.id.toString(),
                                    it.nickname))
                }
            }


            override fun onSessionClosed(errorResult: ErrorResult?) {
                Logger.d(errorResult.toString())
            }


            override fun onFailure(errorResult: ErrorResult) {
                Logger.d("failed to get user info. msg = $errorResult")

                if (errorResult.errorCode == ApiErrorCode.CLIENT_ERROR_CODE) {
                    Logger.d("error failed.")
                } else {
                    navigator.redirectLoginActivity()
                }
            }
        })
    }


}