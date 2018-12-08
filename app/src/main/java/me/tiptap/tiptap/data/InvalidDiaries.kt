package me.tiptap.tiptap.data

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

class InvalidDiaries {

    @SerializedName("date")
    val date = mutableListOf<String>()

    //convert date to body's format.
    fun convertDateToString(rawDate : MutableList<Date>) {
        for (i in 0 until  rawDate.size) {
            date.add(SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(rawDate[i]))
        }
    }
}
