package me.tiptap.tiptap.diarywriting

import android.annotation.TargetApi
import android.databinding.DataBindingUtil
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.ActivityDiaryWritingBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DiaryWritingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDiaryWritingBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary_writing)

        getFormattedDate()

        binding.complete.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View):Unit{
                finish()
            }
        })

    }

    @TargetApi(Build.VERSION_CODES.O)
    fun getFormattedDate() {
        val time = LocalDateTime.now()
        var str = ""
        val monthFormatter = DateTimeFormatter.ofPattern("MM")
        val monthFormatted = time.format(monthFormatter)
        val monthEng:String = getMonth(monthFormatted.toString())

        val yearFormatter = DateTimeFormatter.ofPattern("YYYY")
        val yearFormatted = time.format(yearFormatter)

        str += "$yearFormatted $monthEng"

        val dayFormatter = DateTimeFormatter.ofPattern("dd")
        val dayFormatted = time.format(dayFormatter)

        str += " $dayFormatted " + getDay()

        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val timeFormatted = time.format(timeFormatter)
        str += " $timeFormatted"

        binding.date.setText("$str")
    }
    fun getMonth(month:String) : String{
        when(month) {
            "01" -> return "JANUARY"
            "02" -> return "FEBUARY"
            "03" -> return "MARCH"
            "04" -> return "APRIL"
            "05" -> return "MAY"
            "06" -> return "JUNE"
            "07" -> return "JULY"
            "08" -> return "AUGUST"
            "09" -> return "SEPTEMBER"
            "10" -> return "OCTOBER"
            "11" -> return "NOVEMBER"
            "12" -> return "DECEMBER"
            else -> return "NOTHING"
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun getDay() : String {
        val calendar:Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)

        when(day) {
            Calendar.SUNDAY -> return "SUN"
            Calendar.MONDAY -> return "MON"
            Calendar.TUESDAY -> return "TUE"
            Calendar.WEDNESDAY -> return "WED"
            Calendar.THURSDAY -> return "THU"
            Calendar.FRIDAY -> return "FRI"
            Calendar.SATURDAY -> return "SAT"
            else -> return "NO"
        }

    }
}
