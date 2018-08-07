package me.tiptap.tiptap.common.action

import android.support.annotation.MenuRes
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View

class GeneralActionModeCallback : android.view.ActionMode.Callback {

    private var mode: ActionMode? = null

    @MenuRes
    private var menuResId: Int = 0
    private var title: String = ""
    private var subTitle: String? = null
    private var view: View? = null

    var onActionListener: OnActionListener? = null


    fun startActionMode(view: View, @MenuRes menuResId: Int, title: String, subTitle: String?) {
        this.menuResId = menuResId
        this.title = title
        this.subTitle = subTitle
        this.view = view

        view.startActionMode(this)
    }


    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        this.mode = mode

        mode?.let {
            it.menuInflater.inflate(menuResId, menu)
            it.title = title

            return true
        }
        return false
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

    override fun onActionItemClicked(mode: ActionMode, menuItem: MenuItem): Boolean {
        onActionListener?.onActionItemClicked(menuItem)
        mode.finish()

        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        onActionListener?.onDestroyActionMode(mode)
        this.mode = null
    }


    /**
     * Highlight title
     */
    fun highlightSelectedCountTitle(color: Int, count: Int) {
        view?.let {
            mode?.title = SpannableString(count.toString() + title).apply {

                setSpan(ForegroundColorSpan(
                        ContextCompat.getColor(it.context, color)),
                        0, count.toString().length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }
}