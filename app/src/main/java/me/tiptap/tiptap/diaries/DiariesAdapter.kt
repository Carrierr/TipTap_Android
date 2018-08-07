package me.tiptap.tiptap.diaries


import android.support.v7.widget.RecyclerView
import android.view.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.tiptap.tiptap.R
import me.tiptap.tiptap.common.action.GeneralActionModeCallback
import me.tiptap.tiptap.common.action.OnActionListener
import me.tiptap.tiptap.data.Diary

class DiariesAdapter : RecyclerView.Adapter<DiariesViewHolder>() {

    private val dataSet: MutableList<Diary> = mutableListOf()
    private val checkedDataSet: MutableList<Diary> = mutableListOf() //checked list

    val clickSubject = PublishSubject.create<Diary>()
    val longClickSubject = PublishSubject.create<View>()
    val checkSubject = PublishSubject.create<Diary>()

    var actionModeCallback: GeneralActionModeCallback? = null


    fun addItem(item: Diary) = dataSet.add(item)

    fun addItems(items: MutableList<Diary>) {
        dataSet.addAll(items)
        notifyDataSetChanged()
    }

    private fun updateCheckedItems(item: Diary) {
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
    fun onCheckedChangeEventPublished(item: Diary) {
        updateCheckedItems(item)
        actionModeCallback?.highlightSelectedCountTitle(R.color.colorAccent, checkedDataSet.size)
    }

    /**
     * When item longClick event is published.
     */
    fun onLongClickEventPublished(item: View) {
        if (actionModeCallback == null) {

            changeCheckboxVisibility(true)

            actionModeCallback = GeneralActionModeCallback().apply {
                startActionMode(item, R.menu.menu_action_list, item.context.getString(R.string.app_name), null)

                onActionListener = object : OnActionListener {
                    override fun onDestroyActionMode(mode: ActionMode) {
                        actionModeCallback = null
                        checkedDataSet.clear()
                        changeCheckboxVisibility(false)
                    }

                    override fun onActionItemClicked(item: MenuItem) {
                        if (item.itemId == R.id.menu_action_delete)
                            deleteItems()
                    }
                }
            }
        }
    }

    /**
     * Change all item's checkbox visibility.
     */
    private fun changeCheckboxVisibility(state: Boolean) {
        Observable.fromIterable(dataSet)
                .filter { it.isCheckboxAvailable != state }
                .subscribe {
                    it.isCheckboxAvailable = state
                    notifyDataSetChanged()
                }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiariesViewHolder =
            DiariesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_diary, parent, false))


    override fun onBindViewHolder(holder: DiariesViewHolder, position: Int) {
        holder.apply {
            getItem(position).apply {
                binding?.diary = this

                getClickObservable(this).subscribe(clickSubject)
                getLongClickObservable().subscribe(longClickSubject)
                getCheckObservable(this).subscribe(checkSubject)
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size

    //get item by position
    fun getItem(position: Int) = dataSet[position]

}