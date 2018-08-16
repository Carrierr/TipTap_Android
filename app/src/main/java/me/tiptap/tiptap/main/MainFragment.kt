package me.tiptap.tiptap.main

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.FragmentMainBinding
import me.tiptap.tiptap.diarywriting.DiaryWritingActivity
import java.util.*

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    val postSize: ObservableField<Int> = ObservableField(0) //post size


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.apply {
           date = Date()
            btnMainAdd.setOnClickListener {
                val intent = Intent(context, DiaryWritingActivity::class.java)
                startActivity(intent)
            }
        }

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initToolbar()
    }


    private fun initToolbar() {
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar?.toolbarMain)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.toolbar?.apply {
            textToolbarMainTitle.text = getString(R.string.today)
            textToolbarMainSub.text = getString(R.string.post_count, postSize.get().toString())
        }
    }

}
