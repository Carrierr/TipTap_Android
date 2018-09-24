package me.tiptap.tiptap

import android.app.Activity
import android.app.Application
import android.content.Context
import com.bumptech.glide.Glide
import com.facebook.stetho.Stetho
import com.kakao.auth.KakaoSDK
import me.tiptap.tiptap.common.util.login.KakaoSDKAdapter

class TipTapApplication : Application() {

    companion object {
        private var instance: TipTapApplication? = null

        fun getTipTapApplicationContext(): Context? {
            if (instance == null) {
                instance = TipTapApplication()
            }

            return instance
        }

        /**
         * Get User access token.
         */
        fun getAccessToken(): String {
            val sharedPref = getTipTapApplicationContext()?.getSharedPreferences("auth", Activity.MODE_PRIVATE)
                    ?: return ""
            val token = sharedPref.getString("token", "")

            if (token.isBlank()) throw IllegalStateException("Invalid token form.") else return token
        }

    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        Glide.with(this)
        Stetho.initializeWithDefaults(this)

        KakaoSDK.init(KakaoSDKAdapter())
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }

}