package me.tiptap.tiptap.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.util.exception.KakaoException
import me.tiptap.tiptap.common.util.redirectLoginActivity
import me.tiptap.tiptap.main.MainActivity

class SplashActivity : AppCompatActivity() {

    private val delayMill = 300L //delay milliseconds

    private val callback = object : ISessionCallback {
        override fun onSessionOpened() { //If session is opened, go to main activity.
            goToMainActivity()
        }

        override fun onSessionOpenFailed(exception: KakaoException?) { //go to login activity.
            redirectLoginActivity()
            finish()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Session.getCurrentSession().addCallback(callback)

        scheduleSplashScreen()
    }

    private fun scheduleSplashScreen() {
        Handler().postDelayed({
            if (!Session.getCurrentSession().checkAndImplicitOpen()) { //Check session
                redirectLoginActivity()
                finish()
            }
        }, delayMill)

    }


    private fun goToMainActivity() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()

        Session.getCurrentSession().removeCallback(callback)
    }
}
