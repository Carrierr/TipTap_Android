package me.tiptap.tiptap.common.binding

import android.databinding.BindingAdapter
import android.view.View

@BindingAdapter("onTouchListener")
fun setOnTouchListener(view: View, listener : View.OnTouchListener) {
   view.setOnTouchListener(listener)
}