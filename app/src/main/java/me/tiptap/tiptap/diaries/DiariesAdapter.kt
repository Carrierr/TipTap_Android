package me.tiptap.tiptap.diaries


import android.databinding.ObservableField
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.tiptap.tiptap.R
import me.tiptap.tiptap.data.Diary

class DiariesAdapter : RecyclerView.Adapter<DiariesViewHolder>() {

    private val dataSet: MutableList<Diary> = mutableListOf()
    val checkedDataSet: MutableList<Int> = mutableListOf() //checked list

    val clickSubject = PublishSubject.create<Diary>()
    val checkSubject = PublishSubject.create<Diary>()

    var isCheckboxAvailable = ObservableField<Boolean>(false)
    var isDifferentMonth = ObservableField<Boolean>(false)


    fun addItem(item: Diary) = dataSet.add(item)

    fun addItems(items: MutableList<Diary>) {
        dataSet.addAll(items)
        notifyDataSetChanged()
    }

    private fun updateCheckedItems(item: Diary) {
        if (!checkedDataSet.contains(item.id) && item.isSelected) {
            checkedDataSet.add(item.id)
        } else if (checkedDataSet.contains(item.id) && !item.isSelected) {
            checkedDataSet.remove(item.id)
        }
    }

    fun deleteCheckedItems() {
        if (checkedDataSet.size > 0) {

            dataSet.iterator().run {
                while (this.hasNext()) {
                    val data = this.next()

                    if (checkedDataSet.contains(data.id)) {
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
    fun onCheckedChangeEventPublished(item: Diary) {
        updateCheckedItems(item)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiariesViewHolder =
            DiariesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_diary, parent, false))


    override fun onBindViewHolder(holder: DiariesViewHolder, position: Int) {
        val item = getItem(position)

        holder.apply {
            binding?.diary = item
            binding?.adapter = this@DiariesAdapter

            getClickObservable(item).subscribe(clickSubject)
            getCheckObservable(item).subscribe(checkSubject)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    //get item by position
    fun getItem(position: Int) = dataSet[position]

}