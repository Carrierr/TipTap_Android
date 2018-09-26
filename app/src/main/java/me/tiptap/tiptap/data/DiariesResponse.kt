package me.tiptap.tiptap.data

import com.google.gson.annotations.SerializedName

class DiariesResponse {

    @SerializedName("code")
    val code: String? = null

    @SerializedName("data")
    val data: DiariesSubResponse = DiariesSubResponse()


    class DiariesSubResponse {

        @SerializedName("list")
        val list: MutableList<MonthDiaries>? = null

        @SerializedName("total")
        val total: Int = 0

    }

}