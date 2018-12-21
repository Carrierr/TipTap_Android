package me.tiptap.tiptap.common.binding

import android.databinding.BindingAdapter
import android.graphics.Point
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.util.Log
import android.view.View

@BindingAdapter("constraintTopToBottomOf")
fun setConstraintTopToBottomOf(view : View, conView : View) {
    val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams

    layoutParams.topToBottom = conView.id
}

@BindingAdapter("guideBegin")
fun setGuidePercent(gl : Guideline, margin : Int) {
    gl.setGuidelineBegin(margin)
}