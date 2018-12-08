package me.tiptap.tiptap.data

import com.google.gson.annotations.SerializedName

class User(
        @SerializedName("type")
        val type : String,

        @SerializedName("account")
        val account : String,

        @SerializedName("name")
        val name : String)