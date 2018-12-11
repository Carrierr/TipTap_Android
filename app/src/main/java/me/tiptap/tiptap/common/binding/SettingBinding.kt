package me.tiptap.tiptap.common.binding

import android.databinding.BindingAdapter
import android.widget.CompoundButton


@BindingAdapter("setOnCheckedChangeListener")
fun setOnCheckedChangeListener(view : CompoundButton, listener : CompoundButton.OnCheckedChangeListener) {
    view.setOnCheckedChangeListener(listener)
}