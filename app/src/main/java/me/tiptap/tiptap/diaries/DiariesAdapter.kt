package me.tiptap.tiptap.diaries


import android.databinding.ObservableField
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.tiptap.tiptap.R
import me.tiptap.tiptap.data.Diaries
import me.tiptap.tiptap.data.Diary
import java.util.*

class DiariesAdapter : RecyclerView.Adapter<DiariesViewHolder>() {

    private val dataSet: MutableList<Diaries> = mutableListOf()
    val checkedDataSet: MutableList<Date> = mutableListOf() //checked list

    val clickSubject = PublishSubject.create<Diary>()
    val checkSubject = PublishSubject.create<Diaries>()

    var isCheckboxAvailable = ObservableField<Boolean>(false)


    fun addItems(items: MutableList<Diaries>) {
        dataSet.addAll(items)
        notifyDataSetChanged()


        visibleSideHeader(dataSet.size - items.size)
    }


    private fun visibleSideHeader(position: Int) {
        dataSet[position].isLastDay = true
    }


    private fun updateCheckedItems(item: Diaries) {
        val createdAt: Date? = item.firstLastDiary?.lastDiary?.createdAt

        createdAt?.let {
            if (!checkedDataSet.contains(it) && item.isSelected) {
                checkedDataSet.add(it)
            }
            if (checkedDataSet.contains(it) && !item.isSelected) {
                checkedDataSet.remove(it)
            }
        }
    }


    fun deleteCheckedItems() {
        if (checkedDataSet.size > 0) {

            dataSet.iterator().run {
                while (this.hasNext()) {
                    val data = this.next()

                    if (checkedDataSet.contains(data.firstLastDiary?.lastDiary?.createdAt)) {
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

            item.firstLastDiary?.lastDiary?.let {
                getClickObservable(it).subscribe(clickSubject)
            }
            getCheckObservable(item).subscribe(checkSubject)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    //get item by position
    fun getItem(position: Int) = dataSet[position]

}