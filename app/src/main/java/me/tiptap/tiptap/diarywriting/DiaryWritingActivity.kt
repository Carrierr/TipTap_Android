package me.tiptap.tiptap.diarywriting

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.ActivityDiaryWritingBinding

class DiaryWritingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDiaryWritingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary_writing)

        binding.complete.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View):Unit{
                finish()
            }
        })

    }
}
