package me.tiptap.tiptap.main

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.FragmentMainBinding
import me.tiptap.tiptap.diarywriting.DiaryWritingActivity

class MainFragment : Fragment() {

    private lateinit var binding : FragmentMainBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        binding.fabMain.setOnClickListener {
            val intent = Intent(context, DiaryWritingActivity::class.java)
            startActivity(intent)
        }


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
