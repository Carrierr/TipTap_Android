package me.tiptap.tiptap.diaries

import android.app.DatePickerDialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import me.tiptap.tiptap.R
import me.tiptap.tiptap.common.rx.RxBus
import me.tiptap.tiptap.databinding.ActivityCalendarBinding
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
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, R.style.BaseDatePickerTheme,
                DatePickerDialog.OnDateSetListener { _, pickYear, pickMonth, pickDay ->
                    if (view is TextView) {
                        view.setTextColor(ContextCompat.getColor(this@CalendarActivity, R.color.colorSeaFoamBlue))
                        view.text = getString(R.string.date_format, pickYear, pickMonth + 1, pickDay)
                    }
                }, year, month, day)

        if (!datePickerDialog.isShowing) {
            datePickerDialog.show()
        }
    }
}
