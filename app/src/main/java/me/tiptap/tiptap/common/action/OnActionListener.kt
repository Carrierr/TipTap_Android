package me.tiptap.tiptap.common.action

import android.view.ActionMode
import android.view.MenuItem

interface OnActionListener {

    fun onActionItemClicked(item: MenuItem)
    fun onDestroyActionMode(mode: ActionMode)
}