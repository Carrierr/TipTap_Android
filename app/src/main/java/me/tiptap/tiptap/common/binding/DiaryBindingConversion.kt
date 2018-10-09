package me.tiptap.tiptap.common.binding

import android.databinding.BindingConversion
import java.text.SimpleDateFormat
import java.util.*


@BindingConversion
fun convertDateToString(date : Date) : String =
    SimpleDateFormat("yyyy.MM.dd", Locale.US).format(date)
