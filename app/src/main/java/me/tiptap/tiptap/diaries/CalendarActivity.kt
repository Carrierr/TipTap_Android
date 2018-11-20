package me.tiptap.tiptap.diaries

import android.app.DatePickerDialog
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import me.tiptap.tiptap.R
import me.tiptap.tiptap.common.rx.RxBus
import me.tiptap.tiptap.databinding.ActivityCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calendar)
        binding.apply {
            activity = this@CalendarActivity
            date = Date()
        }

    }

    fun onMenuButtonClick(view: View) {
        when (view.id) {
            R.id.text_cal_cancel -> finish()
            R.id.text_cal_ok -> {
                sendDateInfo()
                finish()
            }
        }
    }


    private fun sendDateInfo() {
        val startDate = binding.textCalStart.text
        val endDate = binding.textCalEnd.text

        RxBus.getInstance().takeBus(arrayListOf(startDate, endDate))
    }


    fun onCalendarOpenClick(view: View) {
        Calendar.getInstance().apply {
            val year = get(Calendar.YEAR)
            val month = get(Calendar.MONTH)
            val day = get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this@CalendarActivity, R.style.BaseDatePickerTheme,
                    DatePickerDialog.OnDateSetListener { _, pickYear, pickMonth, pickDay ->
                        if (view is TextView) {
                            val dateText = getString(R.string.date_format, pickYear, pickMonth + 1, pickDay)

                            if (checkDateIsValid(dateText, view.id)) { //if date is valid.
                                view.setTextColor(ContextCompat.getColor(this@CalendarActivity, R.color.colorSeaFoamBlue))
                                view.text = dateText
                            }
                        }
                    }, year, month, day)

            if (!datePickerDialog.isShowing) {
                datePickerDialog.show()
            }
        }
    }

    /**
     * Check  whether the Date is valid .
     * 1. startDate & endDate can not be over today.
     * 2. startDate must be less than endDate.
     * 3. endDate must be greater than startDate.
     */
    private fun checkDateIsValid(dateText: String, @IdRes viewId: Int): Boolean {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN)
        val pickDate = dateFormat.parse(dateText)

        val startDate = dateFormat.parse(binding.textCalStart.text.toString())
        val endDate = dateFormat.parse(binding.textCalEnd.text.toString())

        return when (viewId) {
            R.id.text_cal_start -> (Date().compareTo(pickDate)) > 0 && (pickDate <= endDate)
            R.id.text_cal_end -> (Date().compareTo(pickDate)) > 0 && (pickDate >= startDate)

            else -> false
        }
    }
}
