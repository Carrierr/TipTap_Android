package me.tiptap.tiptap.login

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.kakao.auth.Session
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.ActivityLoginBinding
import me.tiptap.tiptap.util.getKeyHash

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val callback = SessionCallback(this)

    /**
     * 로그인 버튼 클릭 시 access token 을 요청하도록 설정.
     * @param savedInstanceState 기존 session 정보가 저장된 객체임.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        Log.d("Hash Key", getKeyHash(this)) //해시 키 얻는 함수 호출.

        Session.getCurrentSession().addCallback(callback)
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
    }
}
