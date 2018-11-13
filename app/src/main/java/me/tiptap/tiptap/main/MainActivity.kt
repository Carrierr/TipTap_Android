package me.tiptap.tiptap.main

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import me.tiptap.tiptap.R
import me.tiptap.tiptap.adapter.MainViewPagerAdapter
import me.tiptap.tiptap.databinding.ActivityMainBinding
import me.tiptap.tiptap.onboarding.OnBoardingActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (!isOnBoardingSkipped()) { //If user see onBoarding yet,
            startActivity(Intent(this, OnBoardingActivity::class.java))
            finish()
            return
        }

        initViewPager()

    }

    private fun isOnBoardingSkipped() : Boolean {
        getSharedPreferences(getString(R.string.on_boarding), Context.MODE_PRIVATE).run {
            return this.getBoolean(getString(R.string.skip), false)
        }
    }

    private fun initViewPager() {
        binding.vpMain.apply {
            adapter = MainViewPagerAdapter(this@MainActivity.supportFragmentManager, 3)
            currentItem = 1
        }
    }
}