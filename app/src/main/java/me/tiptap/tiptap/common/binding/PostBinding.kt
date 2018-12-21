package me.tiptap.tiptap.common.binding

import android.databinding.BindingAdapter
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.widget.ImageView
import me.tiptap.tiptap.R

@BindingAdapter("setOnTouchListener")
fun setOnTouchListenerToPost(view : ImageView, value : Boolean) {
    view.setOnTouchListener { _, event ->
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                view.run {
                    //set color filter, when user touch the imageView.
                    setColorFilter(ContextCompat.getColor(view.context, R.color.colorMainGrayDark), android.graphics.PorterDuff.Mode.MULTIPLY)
                    invalidate()
                }
                value
            }
            MotionEvent.ACTION_UP -> {
                view.run {
                    clearColorFilter()
                    invalidate() //This is helpful when you apply more than one color filter
                }
                value
            }
            else -> value
        }
    }
}