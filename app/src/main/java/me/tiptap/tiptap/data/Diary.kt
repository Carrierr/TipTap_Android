package me.tiptap.tiptap.data

import android.net.Uri
import java.util.*

data class Diary(
        val id: Int,
        val date : Date,
        val content: String,
        val location: String,
        val photo: Uri
)
