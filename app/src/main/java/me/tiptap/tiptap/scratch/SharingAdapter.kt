package me.tiptap.tiptap.scratch


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import me.tiptap.tiptap.R
import me.tiptap.tiptap.data.Diary


class SharingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataSet: MutableList<Diary> = mutableListOf()


    fun addItems(items: MutableList<Diary>) {
        val startPosition = itemCount
        dataSet.addAll(items)

        notifyItemRangeInserted(startPosition, itemCount)

    }

    fun deleteAllItems() {
        dataSet.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            SharingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sharing, parent, false))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        if (holder is SharingViewHolder) {
            holder.apply {
                binding?.also {
                    it.diary = item
                    it.idx = (position + 1).toString()
                }
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size

    //get item by position
    fun getItem(position: Int) = dataSet[position]


}