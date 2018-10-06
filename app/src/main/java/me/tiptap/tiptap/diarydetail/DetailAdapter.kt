package me.tiptap.tiptap.diarydetail

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import me.tiptap.tiptap.R
import me.tiptap.tiptap.data.Diary

class DetailAdapter : RecyclerView.Adapter<DetailViewHolder>() {

    private val dataSet = mutableListOf<Diary>()

    
    fun addItems(items: MutableList<Diary>) {
        dataSet.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder =
            DetailViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_diary_detail, parent, false))


    override fun onBindViewHolder(holder: DetailViewHolder, positon: Int) {
        holder.binding?.diary = dataSet[positon]
    }

    override fun getItemCount(): Int = dataSet.size
}