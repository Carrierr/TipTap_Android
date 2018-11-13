package me.tiptap.tiptap.onboarding

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.ActivityOnboardingBinding
import me.tiptap.tiptap.main.MainActivity


class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding)
        binding.activity = this


        initOnBoardingViewPager()
    }

    private fun initOnBoardingViewPager() {
        binding.vpOnBoarding.apply{
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                override fun onPageScrollStateChanged(p0: Int) {
                }
                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                }

                override fun onPageSelected(position: Int) {
                    when (position) {
                        0, 1 -> binding.textNextOnboard.text = getString(R.string.board_next)
                        2 -> binding.textNextOnboard.text = getString(R.string.board_start)
                    }
                }
            })

            adapter = OnBoardingAdapter(supportFragmentManager)
        }
    }

    fun onSkipOnBoardingClick() {
        finishOnBoarding()
    }

    fun onNextOnBoardingClick() {
        val curItem = binding.vpOnBoarding.currentItem

        if (curItem == 2) finishOnBoarding() else binding.vpOnBoarding.currentItem = curItem + 1
    }


    private fun finishOnBoarding() {
        getSharedPreferences(getString(R.string.on_boarding), Activity.MODE_PRIVATE).apply {
            this.edit().run {
                putBoolean(getString(R.string.skip), true)
                apply()
            }
        }

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}