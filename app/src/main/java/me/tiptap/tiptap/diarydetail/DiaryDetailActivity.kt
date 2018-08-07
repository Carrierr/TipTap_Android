package me.tiptap.tiptap.diarydetail

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.ActivityDiaryDetailBinding
import me.tiptap.tiptap.util.setupActionBar

class DiaryDetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDiaryDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary_detail)

        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
        }
    }
}
