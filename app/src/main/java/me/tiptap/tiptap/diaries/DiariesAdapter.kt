package me.tiptap.tiptap.diaries


import android.databinding.ObservableBoolean
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
    val longClickSubject = PublishSubject.create<Boolean>()
    val checkSubject = PublishSubject.create<Diaries>()

    val isCheckboxAvailable = ObservableBoolean(false)


    fun addItems(items: MutableList<Diaries>) {
        val startPosition = itemCount

        dataSet.addAll(items)
        notifyItemRangeInserted(startPosition, itemCount)

        visibleSideHeader(dataSet.size - items.size)
    }


    fun deleteAllItems() {
        dataSet.clear()
        notifyDataSetChanged()
    }


    /**
     * delete by date
     */
    fun deleteItemByDate(date: Date) {
      dataSet.listIterator().run {

            while (this.hasNext()) {
                val data = this.next()
                val position = dataSet.indexOf(data)

                if (data.firstLastDiary?.lastDiary?.createdAt == date) {
                    this.remove()
                    notifyItemRemoved(position)

                    if (data.isLastDay && position < itemCount) {
                        visibleSideHeader(position)
                    }

                    notifyItemRangeChanged(position, itemCount-1)
                }
            }
        }
    }

    /**
     * set visible side header
     */
    private fun visibleSideHeader(position: Int) {
        dataSet[position].isLastDay = true
    }


    /**
     * when item's checkbox is clicked.
     */
    fun updateCheckedItems(item: Diaries) {
        val createdAt: Date? = item.firstLastDiary?.lastDiary?.createdAt

        createdAt?.let {
            if (!checkedDataSet.contains(it) && item.isSelected.get()) {
                checkedDataSet.add(it)
            }
            if (checkedDataSet.contains(it) && !item.isSelected.get()) {
                checkedDataSet.remove(it)
            }
        }
    }

    fun changeDeleteModeState(state: Boolean) {
        isCheckboxAvailable.set(state) //change checkbox available or not

        if (!state) {
            changeCheckboxState(state)
        }
    }


    fun deleteCheckedItems() {
        if (checkedDataSet.isNotEmpty()) {
            dataSet.iterator().run {

                while (this.hasNext()) {
                    val data = this.next()
                    val position = dataSet.indexOf(data)

                    if (checkedDataSet.contains(data.firstLastDiary?.lastDiary?.createdAt)) {
                        this.remove()
                        notifyItemRemoved(position)

                        if (data.isLastDay && position < itemCount) {
                            visibleSideHeader(position)
                        }
                        
                    }
                }
            }
        }
    }


    private fun changeCheckboxState(state: Boolean) {
        Observable.fromIterable(dataSet)
                .filter { data -> data.isSelected.get() != state }
                .doOnComplete {
                    checkedDataSet.clear()
                }
                .subscribe { data ->
                    data.isSelected.set(state)
                }
                .dispose()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiariesViewHolder =
            DiariesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_diary, parent, false))


    override fun onBindViewHolder(holder: DiariesViewHolder, position: Int) {
        val item = dataSet[position]

        holder.apply {
            binding?.also {
                it.diaries = item
                it.adapter = this@DiariesAdapter
            }

            item.firstLastDiary?.lastDiary?.let {
                getClickObservable(it).subscribe(clickSubject)
            }
            getLongClickObservable().subscribe(longClickSubject)
            getCheckObservable(item).subscribe(checkSubject)
        }
    }

    override fun getItemId(position: Int): Long {
        dataSet[position].firstLastDiary?.lastDiary?.let {
            return it.id.toLong()
        }
        return -1
    }

    override fun getItemCount(): Int = dataSet.size
}