package me.tiptap.tiptap.onboarding

import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import me.tiptap.tiptap.R

class OnBoardingAdapter(fm : FragmentManager): FragmentStatePagerAdapter(fm) {


    override fun getItem(position: Int): Fragment? =
        when (position) {
            0 -> OnBoardingFragment1()
            1 -> OnBoardingFragment2()
            2 -> OnBoardingFragment3()
            else -> null
        }


    override fun getCount(): Int = 3

}