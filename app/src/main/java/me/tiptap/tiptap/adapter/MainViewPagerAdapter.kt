package me.tiptap.tiptap.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class MainViewPagerAdapter(fm: FragmentManager, val pageCount: Int) : FragmentStatePagerAdapter(fm) {


    override fun getItem(position: Int): Fragment = when (position) {
        0 -> Fragment()
        1 -> Fragment()
        else -> Fragment()
    }

    override fun getCount(): Int = pageCount
}