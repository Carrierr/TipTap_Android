package me.tiptap.tiptap.data

import okhttp3.MultipartBody
import java.util.*

class Diary() {
    var id: Int = 0
    //real val
    var content: String = ""
    var location: String = ""
    var imageUrl: String? = null

    var latitude: String = ""
    var longitude: String = ""
    var shared: Boolean = true
    var createdAt: Date = Date()
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


