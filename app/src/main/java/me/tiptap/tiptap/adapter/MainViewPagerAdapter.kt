package me.tiptap.tiptap.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import me.tiptap.tiptap.MainFragment
import me.tiptap.tiptap.ScratchFragment
import me.tiptap.tiptap.diaries.DiariesFragment

class MainViewPagerAdapter(fm: FragmentManager, val pageCount: Int) : FragmentStatePagerAdapter(fm) {


    override fun getItem(position: Int): Fragment = when (position) {
        0 -> DiariesFragment()
        1 -> MainFragment()
        2 -> ScratchFragment()
        else -> MainFragment()
    }

    override fun getCount(): Int = pageCount
}