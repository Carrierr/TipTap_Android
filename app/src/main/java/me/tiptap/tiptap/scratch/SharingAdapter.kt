package me.tiptap.tiptap.scratch


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import me.tiptap.tiptap.R
import me.tiptap.tiptap.data.Diary


class SharingAdapter : RecyclerView.Adapter<SharingViewHolder>() {

    private val dataSet: MutableList<Diary> = mutableListOf()


    fun updateItems(items: MutableList<Diary>) {
        if (dataSet.size > 0) {
            dataSet.clear()
        }

        dataSet.addAll(items)

        notifyDataSetChanged()
    }

    fun deleteAllItems() {
        dataSet.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharingViewHolder =
            SharingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sharing, parent, false))


    override fun onBindViewHolder(holder: SharingViewHolder, position: Int) {
        holder.binding?.let {
            it.diary = dataSet[position]
        }
    }

    override fun getItemCount(): Int = dataSet.size

    //get item by position
    fun getItem(position: Int) = dataSet[position]

}