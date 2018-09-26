package me.tiptap.tiptap.data

import com.google.gson.annotations.SerializedName

class FirstLastDiary {

    @SerializedName("lastDiary")
    val lastDiary: Diary? = null

    @SerializedName("firstDiary")
    val firstDiary: Diary? = null
}