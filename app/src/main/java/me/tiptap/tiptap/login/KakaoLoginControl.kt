package me.tiptap.tiptap.login

import android.app.Activity
import android.content.Context
import com.kakao.auth.AuthType
import com.kakao.auth.Session
import com.kakao.usermgmt.LoginButton


class KakaoLoginControl(context: Context) : LoginButton(context) {

    fun call() {
        try {
            val methodGetAuthTypes = LoginButton::class.java.getDeclaredMethod("getAuthTypes")
            methodGetAuthTypes.isAccessible = true
            val authTypes = methodGetAuthTypes.invoke(this) as List<AuthType>

                if (authTypes.size == 1) {
                    Session.getCurrentSession().open(authTypes[0], context as Activity)
                } else {
                    LoginButton::class.java.getDeclaredMethod("onClickLoginButton", LoginActivity::class.java).apply {
                        isAccessible = true
                        invoke(this, authTypes)
                    }
                }
        } catch (e: Exception) {
            Session.getCurrentSession().open(AuthType.KAKAO_ACCOUNT, context as Activity)
        }
    }

}