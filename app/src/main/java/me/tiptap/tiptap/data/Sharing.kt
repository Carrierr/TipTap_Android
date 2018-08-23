package me.tiptap.tiptap.data

import java.util.*

data class Sharing(
        val id: Int,
        val date: Date,
        val content: String,
        val location: String,
        var isSelected: Boolean = false
)