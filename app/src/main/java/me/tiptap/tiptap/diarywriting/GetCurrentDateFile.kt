package me.tiptap.tiptap.diarywriting

import android.annotation.TargetApi
import android.icu.util.Calendar
import android.os.Build
import me.tiptap.tiptap.databinding.ActivityDiaryWritingBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import android.support.v13.view.inputmethod.InputConnectionCompat
import android.support.v4.os.BuildCompat
import android.os.Bundle
import android.support.v13.view.inputmethod.InputContentInfoCompat
import android.support.v13.view.inputmethod.EditorInfoCompat
import android.view.inputmethod.InputConnection
import android.view.inputmethod.EditorInfo
import android.widget.EditText





fun getFormattedDate(binding:ActivityDiaryWritingBinding) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

        binding.date.setText(str)

    } else {
        //TODO("VERSION.SDK_INT < O")
        val year = SimpleDateFormat("yyyy")
        val yearDate = year.format(Date())
        var str = ""
        str += yearDate.toString()

        val month = SimpleDateFormat("MM")
        val monthDate = month.format(Date())
        str += " " + getMonth(monthDate.toString())

        val day = SimpleDateFormat("dd")
        var dayDate = day.format(Date())
        str += " " + dayDate.toString() + " " + getDay()

        val time = SimpleDateFormat("HH:mm")
        val timeDate = time.format(Date())
        str += " " + timeDate.toString()

        binding.date.setText(str)


        //binding.date.setText(currentDate.toString())

    }

}
fun getMonth(month:String) : String{
    when(month) {
        "01" -> return "JANUARY"
        "02" -> return "FEBRUARY"
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

@TargetApi(Build.VERSION_CODES.O)
fun getDay() : String {
    val calendar: Calendar = Calendar.getInstance()
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