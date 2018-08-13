package me.tiptap.tiptap.diarydetail

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_detail_delete) {
            AlertDialog.Builder(this).apply {
                setMessage(R.string.ask_delete)
                setNegativeButton(R.string.cancel, null)
                setPositiveButton(R.string.ok) {_, _ ->
                    finish()
                }

                show()
            }
        }
        return false
    }
}
