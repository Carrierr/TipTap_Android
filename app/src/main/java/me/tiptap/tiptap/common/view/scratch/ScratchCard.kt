/**
 *
 * Copyright 2016 Harish Sridharan
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.tiptap.tiptap.common.view.scratch

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Shader.TileMode
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import me.tiptap.tiptap.R
import java.util.*


class ScratchCard : ImageView {

    interface IRevealListener {
        fun onRevealed(iv: ScratchCard)
        fun onRevealPercentChangedListener(siv: ScratchCard, percent: Float)
    }

    private var mX: Float = 0f
    private var mY: Float = 0f

    /**
     * Bitmap holding the scratch region.
     */
    private var mScratchBitmap: Bitmap? = null

    /**
     * Drawable canvas area through which the scratchable area is drawn.
     */
    private var mCanvas: Canvas = Canvas()

    /**
     * Path holding the erasing path done by the user.
     */
    private var mErasePath: Path = Path()

    /**
     * Path to indicate where the user have touched.
     */
    private var mTouchPath: Path? = null


    /**
     * Paint properties for drawing the scratch area.
     */
    private var mBitmapPaint: Paint? = null

    /**
     * Paint properties for erasing the scratch region.
     */
    private var mErasePaint: Paint = Paint()

    /**
     * Gradient paint properties that lies as a background for scratch region.
     */
    private var mGradientBgPaint: Paint = Paint()

    /**
     * Sample Drawable bitmap having the scratch pattern.
     */
    private var mDrawable: BitmapDrawable? = null

    /**
     * Listener object callback reference to send back the callback when the text has been revealed.
     */
    private var mRevealListener: IRevealListener? = null

    /**
     * Reveal percent value.
     */
    var mRevealPercent: Float = 0.toFloat()

    /**
     * Thread Count
     */
    private var mThreadCount = 0

    val color: Int
        get() = mErasePaint.color

    var isRevealed: Boolean = false //리스너에서 사용


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, set: AttributeSet) : super(context, set) {
        init()
    }


    /**
     * Set the strokes width based on the parameter multiplier.
     * @param multiplier can be 1,2,3 and so on to set the stroke width of the paint.
     */
    private fun setStrokeWidth(multiplier: Int) {
        mErasePaint.strokeWidth = multiplier * STROKE_WIDTH
    }


    /**
     * Initialises the paint drawing elements.
     */
    private fun init() {
        mTouchPath = Path()
        mErasePaint = Paint()

        mErasePaint.apply {
            isAntiAlias = true
            isDither = true
            color = -0x10000
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.BEVEL
            strokeCap = Paint.Cap.ROUND
            xfermode = PorterDuffXfermode(
                    PorterDuff.Mode.CLEAR)
        }

        setStrokeWidth(18)

        mGradientBgPaint = Paint()

        mErasePath = Path()
        mBitmapPaint = Paint(Paint.DITHER_FLAG)

        val scratchNum = (Random().nextInt(10) + 1) % 2

        when (scratchNum) {
            0 -> scratchBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_scratch_01)
            1 -> scratchBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_scratch_02)
        }

        mDrawable = BitmapDrawable(resources, scratchBitmap).apply {
            if (visibility != View.VISIBLE) {
                visibility = View.VISIBLE
            }
        }

        setEraserMode()
    }

    /**
     * Redraw cover.
     */
    fun redrawCover() {
        isRevealed = false
        mRevealPercent = 0f

        init() //call init.
        paintCoverScreen()
    }

    /**
     * Paint cover screen.
     */
    private fun paintCoverScreen() {
        mScratchBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)?.apply {
            val startGradientColor = ContextCompat.getColor(this@ScratchCard.context, R.color.scratch_start_gradient)
            val endGradientColor = ContextCompat.getColor(this@ScratchCard.context, R.color.scratch_end_gradient)

            val rect = Rect(0, 0, width, height)

            mGradientBgPaint.shader = LinearGradient(0.0f, 0.0f, 0.0f, height.toFloat(),
                    startGradientColor, endGradientColor, TileMode.MIRROR)

            mCanvas = Canvas(this).apply {
                drawRect(rect, mGradientBgPaint)
            }

            mDrawable?.run {
                this.bounds = rect
                draw(mCanvas)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        paintCoverScreen() //paint
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.also {
            it.drawBitmap(mScratchBitmap!!, 0f, 0f, mBitmapPaint)
            it.drawPath(mErasePath, mErasePaint)
        }
    }

    private fun touchStart(x: Float, y: Float) {
        mErasePath.also {
            it.reset()
            it.moveTo(x, y)
        }

        mX = x
        mY = y
    }


    fun fadeOutAnimation(view: View, animationDuration: Long) {
        AlphaAnimation(1f, 0f).apply {
            interpolator = AccelerateInterpolator()
            startOffset = animationDuration
            duration = animationDuration
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    view.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })

            view.startAnimation(this)
        }
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mErasePath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y

            drawPath()
        }

        mTouchPath!!.reset()
        mTouchPath!!.addCircle(mX, mY, 30f, Path.Direction.CW)

    }

    private fun drawPath() {
        mErasePath.lineTo(mX, mY)
        // commit the path to our offscreen
        mCanvas.drawPath(mErasePath, mErasePaint)
        // kill this so we don't double draw
        mTouchPath?.reset()
        mErasePath.reset()
        mErasePath.moveTo(mX, mY)

        checkRevealed()
    }

    private fun touchUp() {
        drawPath()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                drawPath()
                invalidate()
            }
        }
        return true
    }

    private fun setEraserMode() {
        mErasePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    fun setRevealListener(listener: IRevealListener) {
        this.mRevealListener = listener
    }


    @SuppressLint("StaticFieldLeak")
    private fun checkRevealed() {
        if (!isRevealed && mRevealListener != null) {

            val bounds = getImageBounds()
            val left = bounds[0]
            val top = bounds[1]
            val width = bounds[2] - left
            val height = bounds[3] - top

            // Do not create multiple calls to compare.
            if (mThreadCount > 1) {
                Log.d("Captcha", "Count greater than 1")
                return
            }

            mThreadCount++

            object : AsyncTask<Int, Void, Float>() {
                override fun doInBackground(vararg params: Int?): Float? {
                    if (mRevealPercent == 1.0f) {
                        mRevealListener?.onRevealed(this@ScratchCard)
                    }

                    try {
                        val leftLine = params[0]
                        val topLine = params[1]
                        val widthLine = params[2]
                        val heightLine = params[3]

                        val croppedBitmap = Bitmap.createBitmap(mScratchBitmap, leftLine!!, topLine!!, widthLine!!, heightLine!!)

                        return BitmapUtils.getTransparentPixelPercent(croppedBitmap)
                    } finally {
                        mThreadCount--
                    }
                }

                public override fun onPostExecute(percentRevealed: Float?) {
                    // check if not revealed before.
                    if (!isRevealed) {
                        val oldValue = mRevealPercent
                        mRevealPercent = percentRevealed!!

                        if (oldValue != percentRevealed) {
                            mRevealListener!!.onRevealPercentChangedListener(this@ScratchCard, percentRevealed)
                        }
                    }
                }
            }.execute(left, top, width, height)

        }
    }

    private fun getImageBounds(): IntArray {
        val vWidth = width - paddingLeft - paddingRight
        val vHeight = height - paddingBottom - paddingTop

        val centerX = vWidth / 2
        val centerY = vHeight / 2

        val bounds = mDrawable!!.bounds

        var width = mDrawable!!.intrinsicWidth
        var height = mDrawable!!.intrinsicHeight

        if (width <= 0) {
            width = bounds.right - bounds.left
        }

        if (height <= 0) {
            height = bounds.bottom - bounds.top
        }

        val left: Int
        val top: Int

        if (height > vHeight) {
            height = vHeight
        }

        if (width > vWidth) {
            width = vWidth
        }


        val scaleType = scaleType

        when (scaleType) {
            ImageView.ScaleType.FIT_START -> {
                left = paddingLeft
                top = centerY - height / 2
            }
            ImageView.ScaleType.FIT_END -> {
                left = vWidth - paddingRight - width
                top = centerY - height / 2
            }
            ImageView.ScaleType.CENTER -> {
                left = centerX - width / 2
                top = centerY - height / 2
            }
            else -> {
                left = paddingLeft
                top = paddingTop
                width = vWidth
                height = vHeight
            }
        }

        return intArrayOf(left, top, left + width, top + height)
    }

    companion object {
        const val STROKE_WIDTH = 12f
        private const val TOUCH_TOLERANCE = 4f
        lateinit var scratchBitmap: Bitmap
    }

}
