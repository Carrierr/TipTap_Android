package me.tiptap.tiptap.diaries

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import me.tiptap.tiptap.databinding.HeaderDiaryBinding

class DiariesHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding: HeaderDiaryBinding? = DataBindingUtil.bind(itemView)
}