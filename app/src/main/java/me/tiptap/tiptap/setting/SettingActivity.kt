package me.tiptap.tiptap.setting

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.ActivitySettingBinding
import me.tiptap.tiptap.util.setupActionBar

class SettingActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)

        initToolbar()
    }

    private fun initToolbar() {
        setupActionBar(R.id.toolbar_setting) {
            setDisplayShowTitleEnabled(false)
        }
    }
}
