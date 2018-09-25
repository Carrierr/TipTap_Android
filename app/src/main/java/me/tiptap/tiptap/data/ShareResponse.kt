package me.tiptap.tiptap.data

import com.google.gson.annotations.SerializedName


class ShareResponse {

    @SerializedName("code")
    val code : String?=null

    @SerializedName("data")
    val data : ShareSubResponse ?=null



    class ShareSubResponse {

        @SerializedName("data")
        val shareDiaries : MutableList<ShareDiary?> = mutableListOf()
    }

}


