package me.tiptap.tiptap

import android.app.Application
import android.content.Context
import com.kakao.auth.KakaoSDK
import me.tiptap.tiptap.login.KakaoSDKAdapter

class TipTapApplication : Application() {

    companion object {
        private var instance: TipTapApplication? = null

        fun getTipTapApplicationContext(): Context? {
            if (instance == null) {
                instance = TipTapApplication()
            }

            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        KakaoSDK.init(KakaoSDKAdapter())
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }

}