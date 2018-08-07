package me.tiptap.tiptap.diarydetail

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.ActivityDiaryDetailBinding

class DiaryDetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDiaryDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary_detail)
    }
}
