package me.tiptap.tiptap.setting

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.UnLinkResponseCallback
import com.kakao.util.helper.log.Logger
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.ActivitySettingBinding
import me.tiptap.tiptap.util.redirectLoginActivity
import me.tiptap.tiptap.util.setupActionBar

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        binding.activity = this

        initToolbar()
    }

    private fun initToolbar() {
        setupActionBar(R.id.toolbar_setting) {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    //logout.
    fun onLogoutButtonClick() {
//        UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
//            override fun onCompleteLogout() {
//                redirectLoginActivity()
//            }
//        })
        UserManagement.getInstance().requestUnlink(object : UnLinkResponseCallback() {
            override fun onSuccess(result: Long?) {
                Logger.d("Unlink is successfully completed.")
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                redirectLoginActivity()
            }

            override fun onNotSignedUp() {
                redirectLoginActivity()
            }

        })
    }

}
