package me.tiptap.tiptap.diaries

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.reactivex.subjects.PublishSubject
import me.tiptap.tiptap.R
import me.tiptap.tiptap.data.Diary

class DiariesAdapter : RecyclerView.Adapter<DiariesViewHolder>() {

    private val dataSet: MutableList<Diary> = mutableListOf()
    val clickSubject: PublishSubject<Diary> = PublishSubject.create()


    fun addItems(items : MutableList<Diary>) {
        dataSet.addAll(items)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiariesViewHolder =
            DiariesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_diary, parent, false))


    override fun onBindViewHolder(holder: DiariesViewHolder, position: Int) {
        holder.apply {
            dataSet[position].apply {
                binding?.diary = this
                getClickObservable(this).subscribe(clickSubject)
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size

    //get item by position
    fun getItem(position : Int) = dataSet[position]

}