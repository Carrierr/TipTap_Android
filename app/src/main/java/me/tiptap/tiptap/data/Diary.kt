package me.tiptap.tiptap.data

import android.net.Uri
import okhttp3.MultipartBody
import java.util.*

class Diary() {
    var id: Int = 0
    var date: Date? = null
    var photo: Uri? = null
    //real val
    var content: String = ""
    var location: String = ""
    var imageUrl: String? = null

    var latitude: String = ""
    var longitude: String = ""
    var shared: Boolean = true
    var createdAt: Date ?=null
    var updatedAt: Date ?=null
    var diaryFile: MultipartBody.Part? = null

    var todayIndex =0

    var isSelected: Boolean = false


    constructor(id : Int, content : String, location : String, latitude : String, longitude : String) : this() {
        this.id = id
        this.content =content
        this.location = location
        this.latitude = latitude
        this.longitude = longitude
    }

}


