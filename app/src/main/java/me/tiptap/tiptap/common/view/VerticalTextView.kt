package me.tiptap.tiptap.common.view

import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet



class VerticalTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(canvas: Canvas) {
        paint.apply {
            color = currentTextColor
            drawableState = getDrawableState()
        }
        canvas.run {
            save()

            translate(width.toFloat(), 0f)
            rotate(90f)

            translate(compoundPaddingLeft.toFloat(), extendedPaddingTop.toFloat())

            layout.draw(this)
            restore()
        }
    }
}