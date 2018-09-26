package me.tiptap.tiptap.data

import com.google.gson.annotations.SerializedName

//이게 diaries 어댑터에 들어가야함.
class Diaries {

    @SerializedName("day")
    val day : String =""

    @SerializedName("diaryDatas")
    val firstLastDiary : FirstLastDiary? =null

    var isSelected: Boolean = false
    var isLastDay : Boolean = false
}