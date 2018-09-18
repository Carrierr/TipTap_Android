package me.tiptap.tiptap.diarydetail

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.ActivityDiaryDetailBinding
import me.tiptap.tiptap.common.util.setupActionBar
import java.util.*

class DiaryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiaryDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary_detail)

        setupActionBar(R.id.toolbar_detail) {
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.ic_back_white)
            setDisplayHomeAsUpEnabled(true)
        }

        binding.apply {
            activity = this@DiaryDetailActivity
            date = Date()
        }
    }

    fun onDeleteButtonClick() {
        AlertDialog.Builder(this).apply {
            setMessage(R.string.ask_delete)
            setNegativeButton(R.string.cancel, null)
            setPositiveButton(R.string.ok) {_, _ ->
                //do something
                finish()
            }

            show()
        }
    }
}
