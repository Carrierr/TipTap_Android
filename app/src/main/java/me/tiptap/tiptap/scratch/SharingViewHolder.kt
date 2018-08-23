package me.tiptap.tiptap.scratch

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import me.tiptap.tiptap.databinding.ItemSharingBinding

class SharingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding: ItemSharingBinding? = DataBindingUtil.bind(itemView)
}