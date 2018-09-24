package me.tiptap.tiptap.common.binding

import android.databinding.BindingAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("writeDate")
fun convertWriteDateFormat(view: TextView, date: Date) {
    view.text = SimpleDateFormat("yyyy MMM dd - hh:mm", Locale.US).format(date)
}