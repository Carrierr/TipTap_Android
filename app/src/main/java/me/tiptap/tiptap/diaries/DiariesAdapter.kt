package me.tiptap.tiptap.diaries


import android.databinding.ObservableField
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.tiptap.tiptap.R
import me.tiptap.tiptap.data.Diaries

class DiariesAdapter : RecyclerView.Adapter<DiariesViewHolder>() {

    private val dataSet: MutableList<Diaries> = mutableListOf()
    val checkedDataSet: MutableList<String> = mutableListOf() //checked list

    val clickSubject = PublishSubject.create<Diaries>()
    val checkSubject = PublishSubject.create<Diaries>()

    var isCheckboxAvailable = ObservableField<Boolean>(false)



    fun addItems(items: MutableList<Diaries>) {
        dataSet.addAll(items)
        Log.d("Res", "size : ${items.size}")

        visibleSideHeader(dataSet.size-items.size)

        notifyDataSetChanged()
    }


    private fun visibleSideHeader(position : Int) {
        dataSet[position].isLastDay = true
    }


    private fun updateCheckedItems(item: Diaries) {
        if (!checkedDataSet.contains(item.day) && item.isSelected) {
            checkedDataSet.add(item.day)
        } else if (checkedDataSet.contains(item.day) && !item.isSelected) {
            checkedDataSet.remove(item.day)
        }
    }


    fun deleteCheckedItems() {
        if (checkedDataSet.size > 0) {

            dataSet.iterator().run {
                while (this.hasNext()) {
                    val data = this.next()

                    if (checkedDataSet.contains(data.day)) {
                        this.remove()
                    }
                }

                notifyDataSetChanged()
            }
        }
    }


    fun changeCheckboxState(state: Boolean) {
        Observable.fromIterable(dataSet)
                .filter { data -> data.isSelected != state }
                .doOnComplete {
                    notifyDataSetChanged()
                    checkedDataSet.clear()
                }
                .subscribe { item ->
                    item.isSelected = state
                }
    }

    /**
     * When item Checked changed event is published.
     */
    fun onCheckedChangeEventPublished(item: Diaries) {
        updateCheckedItems(item)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiariesViewHolder =
            DiariesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_diary, parent, false))


    override fun onBindViewHolder(holder: DiariesViewHolder, position: Int) {
        val item = getItem(position)

        holder.apply {
            binding?.diaries = item
            binding?.adapter = this@DiariesAdapter

            getClickObservable(item).subscribe(clickSubject)
            getCheckObservable(item).subscribe(checkSubject)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    //get item by position
    fun getItem(position: Int) = dataSet[position]

}