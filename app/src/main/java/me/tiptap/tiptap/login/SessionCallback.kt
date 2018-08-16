package me.tiptap.tiptap.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.kakao.auth.ISessionCallback
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.ApiErrorCode
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import com.kakao.util.helper.log.Logger
import me.tiptap.tiptap.main.MainActivity
import me.tiptap.tiptap.util.redirectLoginActivity

class SessionCallback(val activity: AppCompatActivity) : ISessionCallback {

    override fun onSessionOpenFailed(exception: KakaoException?) {
        if (exception != null) Logger.e(exception)
    }

    override fun onSessionOpened() {
        requestMe()
    }

    private fun requestMe() {
        UserManagement.getInstance().me(object : MeV2ResponseCallback() {
            override fun onSuccess(result: MeV2Response?) {
                activity.apply {
                    startActivity(Intent(activity, MainActivity::class.java))
                    finish()
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
                    activity.redirectLoginActivity()
                }
            }
        })
    }


}