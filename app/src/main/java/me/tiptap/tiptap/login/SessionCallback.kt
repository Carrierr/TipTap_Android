package me.tiptap.tiptap.login

import android.content.Context
import android.content.Intent
import com.kakao.auth.ISessionCallback
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.ApiErrorCode
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import com.kakao.util.helper.log.Logger
import me.tiptap.tiptap.main.MainActivity

class SessionCallback(val context: Context) : ISessionCallback {

    override fun onSessionOpenFailed(exception: KakaoException?) {
        if (exception != null) Logger.e(exception)
    }

    override fun onSessionOpened() {
        context.startActivity(Intent(context, MainActivity::class.java))
    }

    private fun requestMe() {
        UserManagement.getInstance().me(object : MeV2ResponseCallback() {

            override fun onSuccess(result: MeV2Response?) {

                result?.let {
                    val serialNumber = result.id
                    val nickname = result.nickname

                    val account = result.kakaoAccount
                    val email = account.email
                    val birth = account.birthday
                }
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                Logger.d(errorResult.toString())
            }

            override fun onFailure(errorResult: ErrorResult) {
                Logger.d("failed to get user info. msg = $errorResult")

                if (errorResult.errorCode == ApiErrorCode.CLIENT_ERROR_CODE) {
                    Logger.d("error failed.")
                }
            }
        })
    }


}