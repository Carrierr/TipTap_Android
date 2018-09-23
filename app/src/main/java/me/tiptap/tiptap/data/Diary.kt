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
    var createdAt: String = ""
    var updatedAt: String = ""
    var diaryFile: MultipartBody.Part? = null

    var isSelected: Boolean = false

    //tempConstructors
    constructor(id: Int, date: Date, content: String, location: String, photo: Uri) : this() {
        this.id = id
        this.date = date
        this.content = content
        this.location = location
        this.photo = photo
    }


    constructor(id : Int, content : String, location : String, latitude : String, longitude : String) : this() {
        this.id = id
        this.content =content
        this.location = location
        this.latitude = latitude
        this.longitude = longitude
    }

}


