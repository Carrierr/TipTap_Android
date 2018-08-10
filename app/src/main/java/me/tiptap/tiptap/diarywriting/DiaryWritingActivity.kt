package me.tiptap.tiptap.diarywriting

import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.ActivityDiaryWritingBinding





class DiaryWritingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDiaryWritingBinding

    private val PERMISSION_CODE = 100

    // Int constant that's used to handle the result back when an image is selected from the
    // device's gallery.
    private val RESULT_LOAD_IMAGE = 1

    private val mImageView: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary_writing)

        getFormattedDate(binding)

        binding.complete.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View):Unit{
                finish()
            }
        })

        binding.location.setOnClickListener(object: View.OnClickListener{
            override fun onClick(view: View):Unit{
                val alert = AlertDialog.Builder(this@DiaryWritingActivity)
                alert.setTitle("위치입력")

                val input = EditText(this@DiaryWritingActivity)
                alert.setView(input)

                alert.setNegativeButton("취소", null)
                alert.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, whichButton ->
                    val place = input.text.toString()
                    // Do something with value!
                    binding.location.setText(place)
                })

                alert.show()
            }
        })

    }
}
