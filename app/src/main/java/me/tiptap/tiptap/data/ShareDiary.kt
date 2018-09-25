package me.tiptap.tiptap.data

import java.util.*


class ShareDiary {

    var id: Int = 0
    var content: String = ""

    var location: String = ""

    var imageUrl: String? = null

    var latitude: String = ""
    var longitude: String = ""

    var shared: Boolean = true

    var createdAt: Date?=null
    var updatedAt: Date?=null
}