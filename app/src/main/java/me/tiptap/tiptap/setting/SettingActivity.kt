package me.tiptap.tiptap.setting

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.CompoundButton
import com.google.gson.JsonObject
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.kakao.usermgmt.callback.UnLinkResponseCallback
import com.kakao.util.helper.log.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import me.tiptap.tiptap.R
import me.tiptap.tiptap.TipTapApplication
import me.tiptap.tiptap.common.network.DiaryApi
import me.tiptap.tiptap.common.network.ServerGenerator
import me.tiptap.tiptap.databinding.ActivitySettingBinding
import me.tiptap.tiptap.common.util.redirectLoginActivity
import me.tiptap.tiptap.common.util.setupActionBar

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    private val service = ServerGenerator.createService(DiaryApi::class.java)
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        binding.activity = this

        initToolbar()

        getShareValue()
    }

    private fun initToolbar() {
        setupActionBar(R.id.toolbar_setting) {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setDiaryShared(isShared : Boolean) {
        val observable = when(isShared) {
            true ->  service.setShareOnDiary(TipTapApplication.getAccessToken())
            false ->  service.setShareOffDiary(TipTapApplication.getAccessToken())
        }

        disposables.add(observable
                .subscribeOn(Schedulers.io())
                .doOnError { e -> e.printStackTrace() }
                .filter { task -> task.get(getString(R.string.code)).asString != "1000" }
                .subscribe { task ->
                    Log.d(task.get(getString(R.string.code)).asString, task.get(getString(R.string.desc)).asString)
                })
    }


    //logout.
    fun onLogoutButtonClick() {
        UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
            override fun onCompleteLogout() {
                redirectLoginActivity()
            }
        })
    }

    private fun getShareValue() {
        getSharedPreferences(getString(R.string.account_key), Activity.MODE_PRIVATE).apply {
            val shared = getBoolean(getString(R.string.shared_key), true)

            binding.switchSettingShare.isChecked = shared
        }
    }

    private fun saveShareValue() {
        val sharedPref = getSharedPreferences(getString(R.string.account_key), Activity.MODE_PRIVATE)

        with(sharedPref.edit()) {
            putBoolean(getString(R.string.shared_key), binding.switchSettingShare.isChecked)
            apply()
        }
    }

    //share
    fun onShareChecked(view: CompoundButton, isChecked: Boolean) {
        setDiaryShared(isChecked)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    override fun onStop() {
        super.onStop()

        saveShareValue()
        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()

        disposables.dispose()
    }

}
