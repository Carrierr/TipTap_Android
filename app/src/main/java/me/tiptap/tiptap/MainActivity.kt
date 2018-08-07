package me.tiptap.tiptap

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.tiptap.tiptap.adapter.MainViewPagerAdapter
import me.tiptap.tiptap.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

       initViewPager()
    }

    private fun initViewPager() {
        binding.vpMain.apply {
            adapter = MainViewPagerAdapter(this@MainActivity.supportFragmentManager, 3)
            currentItem = 1
        }
    }
}
