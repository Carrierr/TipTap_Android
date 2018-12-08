package me.tiptap.tiptap.data

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import java.util.*

class Diary {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("content")
    var content: String = ""

    @SerializedName("location")
    var location: String = ""

    @SerializedName("imageUrl")
    var imageUrl: String? = null

    @SerializedName("latitude")
    var latitude: String = ""

    @SerializedName("longitude")
    var longitude: String = ""

    @SerializedName("shared")
    var shared: Boolean = true

    @SerializedName("createdAt")
    var createdAt: Date = Date()

    @SerializedName("updatedAt")
    var updatedAt: Date ?=null

    @SerializedName("diaryFile")
    var diaryFile: MultipartBody.Part? = null

    var isSelected: Boolean = false
}


