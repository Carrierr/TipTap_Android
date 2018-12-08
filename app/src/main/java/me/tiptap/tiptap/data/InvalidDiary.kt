package me.tiptap.tiptap.data

import com.google.gson.annotations.SerializedName

/**
 * Invalid Diary for delete api.
 */
class InvalidDiary(
        @SerializedName("id")
        val id : MutableList<Int>)