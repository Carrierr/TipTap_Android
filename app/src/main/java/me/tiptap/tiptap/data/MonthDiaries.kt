package me.tiptap.tiptap.data

import com.google.gson.annotations.SerializedName

class MonthDiaries {

    @SerializedName("year")
    val year : String=""

    @SerializedName("month")
    val month : String =""

    @SerializedName("datas")
    val diariesOfDay : MutableList<Diaries>?=null
}