package me.tiptap.tiptap.scratch


import android.databinding.ObservableField
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import me.tiptap.tiptap.R
import me.tiptap.tiptap.data.Sharing
import me.tiptap.tiptap.diaries.DiariesHeaderViewHolder
import me.tiptap.tiptap.diaries.DiariesViewHolder

class SharingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataSet: MutableList<Sharing> = mutableListOf()

    var isCheckboxAvailable: ObservableField<Boolean> = ObservableField(false)

    private val HEADER = 0 //view type
    private val ITEM = 1


    fun addItem(item: Sharing) = dataSet.add(item)

    fun addItems(items: MutableList<Sharing>) {
        dataSet.addAll(items)
        notifyDataSetChanged()
    }

    //test
    override fun getItemViewType(position: Int): Int =
            when {
                position == dataSet.size - 1 -> ITEM
                position == 0 -> HEADER
                dataSet[position].content.contains("5") -> HEADER
                else -> ITEM
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                HEADER -> SharingHeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_sharing, parent, false))
                ITEM -> SharingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sharing, parent, false))
                else -> SharingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sharing, parent, false))
            }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        if (holder is SharingViewHolder) {
            holder.apply {
                binding?.sharing = item
                binding?.adapter = this@SharingAdapter

            }
        } else if (holder is SharingHeaderViewHolder) {
            holder.binding?.sharing = item
        }
    }

    override fun getItemCount(): Int = dataSet.size
    //get item by position
    fun getItem(position: Int) = dataSet[position]


}