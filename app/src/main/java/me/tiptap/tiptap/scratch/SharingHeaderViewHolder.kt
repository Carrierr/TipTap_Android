package me.tiptap.tiptap.scratch

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import me.tiptap.tiptap.databinding.HeaderSharingBinding

class SharingHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding: HeaderSharingBinding? = DataBindingUtil.bind(itemView)
}