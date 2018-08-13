package me.tiptap.tiptap.diarywriting

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.ActivityDiaryWritingBinding
import java.io.IOException


class DiaryWritingActivity : AppCompatActivity()  {

    private lateinit var binding : ActivityDiaryWritingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary_writing)

        getFormattedDate(binding)

        binding.textComplete.setOnClickListener { finish() }

        binding.textLocation.setOnClickListener {
            val alert = AlertDialog.Builder(this@DiaryWritingActivity)
            alert.setTitle(R.string.location_confirm)

            val input = EditText(this@DiaryWritingActivity)
            alert.setView(input)

            alert.setNegativeButton(R.string.cancle, null)
            alert.setPositiveButton(R.string.ok) { _, _ ->
                val place = input.text.toString()
                // Do something with value!
                binding.textLocation.text = place
            }

            alert.show()
        }
        binding.imgGallery.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
        }

        binding.editDiaryWrite.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.textKeyboard.text = p0?.length.toString() + "/800"
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.textKeyboard.text = "0/800"
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.textKeyboard.setText(p0)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                if(data !=null){
                    try{
                        val bitmap:Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data )
                        binding.imgMyPicture.setImageBitmap(bitmap)
                    }catch (e:IOException){
                        e.printStackTrace()
                    }
                }else if(resultCode == Activity.RESULT_CANCELED){
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
