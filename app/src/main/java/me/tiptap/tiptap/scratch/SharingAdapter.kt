package me.tiptap.tiptap.scratch


import android.databinding.ObservableField
import android.support.v7.widget.RecyclerView
import android.view.*
import io.reactivex.subjects.PublishSubject
import me.tiptap.tiptap.R
import me.tiptap.tiptap.common.action.GeneralActionModeCallback
import me.tiptap.tiptap.common.action.OnActionListener
import me.tiptap.tiptap.data.Sharing
import me.tiptap.tiptap.diaries.DiariesHeaderViewHolder
import me.tiptap.tiptap.diaries.DiariesViewHolder

class SharingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataSet: MutableList<Sharing> = mutableListOf()
    private val checkedDataSet: MutableList<Sharing> = mutableListOf() //checked list

    val clickSubject = PublishSubject.create<Sharing>()
    val longClickSubject = PublishSubject.create<View>()
    val checkSubject = PublishSubject.create<Sharing>()

    var actionModeCallback: GeneralActionModeCallback? = null
    var isCheckboxAvailable: ObservableField<Boolean> = ObservableField(false)

    private val HEADER = 0 //view type
    private val ITEM = 1


    fun addItem(item: Sharing) = dataSet.add(item)

    fun addItems(items: MutableList<Sharing>) {
        dataSet.addAll(items)
        notifyDataSetChanged()
    }

    private fun updateCheckedItems(item: Sharing) {
        if (!checkedDataSet.contains(item) && item.isSelected) {
            checkedDataSet.add(item)
        } else if (checkedDataSet.contains(item) && !item.isSelected) {
            checkedDataSet.remove(item)
        }
    }

    private fun deleteItems() {
        if (checkedDataSet.size > 0) {
            dataSet.removeAll(checkedDataSet)
            notifyDataSetChanged()
        }
    }

    /**
     * When item Checked changed event is published.
     */
    fun onCheckedChangeEventPublished(item: Sharing) {
        updateCheckedItems(item)
        actionModeCallback?.highlightSelectedCountTitle(R.color.colorAccent, checkedDataSet.size)
    }

    /**
     * When item longClick event is published.
     */
    fun onLongClickEventPublished(item: View) {
        if (actionModeCallback == null) {
            isCheckboxAvailable.set(true)

            actionModeCallback = GeneralActionModeCallback().apply {
                startActionMode(item, R.menu.menu_action_list, item.context.getString(R.string.app_name), null)

                onActionListener = object : OnActionListener {
                    override fun onDestroyActionMode(mode: ActionMode) {
                        actionModeCallback = null
                        checkedDataSet.clear()
                        isCheckboxAvailable.set(false)
                    }

                    override fun onActionItemClicked(item: MenuItem) {
                        if (item.itemId == R.id.menu_action_delete)
                            deleteItems()
                    }
                }
            }
        }
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
                HEADER -> DiariesHeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_sharing, parent, false))
                ITEM -> DiariesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sharing, parent, false))
                else -> DiariesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sharing, parent, false))
            }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        if (holder is SharingViewHolder) {
            holder.apply {
                binding?.sharing = item
                binding?.adapter = this@SharingAdapter

                getClickObservable(item).subscribe(clickSubject)
                getLongClickObservable().subscribe(longClickSubject)
                getCheckObservable(item).subscribe(checkSubject)
            }
        } else if (holder is SharingHeaderViewHolder) {
            holder.binding?.sharing = item
        }
    }

    override fun getItemCount(): Int = dataSet.size

    //get item by position
    fun getItem(position: Int) = dataSet[position]

}