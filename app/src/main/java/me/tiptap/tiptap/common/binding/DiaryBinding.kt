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


