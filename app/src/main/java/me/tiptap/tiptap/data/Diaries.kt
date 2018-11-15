package me.tiptap.tiptap.data

import android.databinding.ObservableBoolean
import com.google.gson.annotations.SerializedName

class Diaries {

    @SerializedName("day")
    val day : String =""

    @SerializedName("dataCount")
    val dataCount : Int =0

    @SerializedName("diaryDatas")
    val firstLastDiary : FirstLastDiary? =null

    var isSelected = ObservableBoolean(false)
    var isLastDay : Boolean = false
}