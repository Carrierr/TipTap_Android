package me.tiptap.tiptap.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TodayResponse {

    @SerializedName("code")
    val code: String? = null

    @SerializedName("data")
    val data: TodaySubResponse = TodaySubResponse()



    class TodaySubResponse {
        @SerializedName("list")
        val diaries: MutableList<Diary> = mutableListOf()


        @SerializedName("stamp")
        @Expose
        val stamp: MutableList<String> = mutableListOf()
    }
}