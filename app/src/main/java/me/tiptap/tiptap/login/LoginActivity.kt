package me.tiptap.tiptap.login

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.JsonObject
import com.kakao.auth.Session
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import me.tiptap.tiptap.R
import me.tiptap.tiptap.common.network.DiaryApi
import me.tiptap.tiptap.common.network.ServerGenerator
import me.tiptap.tiptap.common.util.login.KaKaoSessionCallback
import me.tiptap.tiptap.common.util.login.LoginNavigator
import me.tiptap.tiptap.data.User
import me.tiptap.tiptap.databinding.ActivityLoginBinding
import me.tiptap.tiptap.main.MainActivity

class LoginActivity : AppCompatActivity(), LoginNavigator {

    private lateinit var binding: ActivityLoginBinding

    private val callback = KaKaoSessionCallback(this)
    private val service: DiaryApi = ServerGenerator.createService(DiaryApi::class.java) //서비스 객체

    private val disposables = CompositeDisposable()


    /**
     * 로그인 버튼 클릭 시 access token 을 요청하도록 설정.
     * @param savedInstanceState 기존 session 정보가 저장된 객체임.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.btnLogin.setOnClickListener {
            KakaoLoginControl(this@LoginActivity).call()
        }
        Session.getCurrentSession().addCallback(callback)
    }

    /**
     * Save token
     */
    private fun saveToken(token: String) {
        getSharedPreferences(getString(R.string.auth), Activity.MODE_PRIVATE).apply {
            this.edit().run {
                putString(getString(R.string.token), token)
                apply()
            }
        }
    }


    /**
     * Get token
     */
    override fun getAccessToken(user: User) {
        disposables.add(service.login(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : DisposableObserver<JsonObject>() {
                    override fun onComplete() {
                        this@LoginActivity.startActivity()
                    }

                    override fun onNext(task: JsonObject) {
                        saveToken(task.getAsJsonObject(getString(R.string.data)).get(getString(R.string.token)).asString)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                }))
    }


    //Util's redirect login activity
    override fun redirectLoginActivity() {
        redirectLoginActivity()
    }

    //StartActivity.
    override fun startActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {

            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onDestroy() {
        super.onDestroy()

        Session.getCurrentSession().removeCallback(callback)
        disposables.dispose()
    }
}
