package me.tiptap.tiptap.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DiaryResponse {

    @SerializedName("code")
    val code: String? = null

    @SerializedName("data")
    val data: DiarySubResponse = DiarySubResponse()


    class DiarySubResponse {
        @SerializedName("list")
        val diaries: MutableList<Diary> = mutableListOf()


        @SerializedName("stamp")
        @Expose
        val stamp: MutableList<String> = mutableListOf()
    }
}