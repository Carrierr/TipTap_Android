package me.tiptap.tiptap.common.binding

import android.databinding.BindingAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("day")
fun extractDayFromDate(view: TextView, date: Date) {
    view.text = SimpleDateFormat("dd", Locale.KOREAN).format(date)
}

@BindingAdapter("header")
fun extractHeaderFromDate(view: TextView, date: Date) {
    view.text = SimpleDateFormat("MMM", Locale.US).format(date)
}

@BindingAdapter("year")
fun extractYearFromDate(view : TextView, date : Date) {
    view.text = SimpleDateFormat("yy`", Locale.KOREAN).format(date)
}

@BindingAdapter("dayOfMonth")
fun extractDayOfMonthFromDate(view : TextView, date : Date) {
    view.text = SimpleDateFormat("MMM \ndd", Locale.US).format(date)
}

