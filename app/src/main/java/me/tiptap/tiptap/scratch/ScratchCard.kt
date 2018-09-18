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
package me.tiptap.tiptap.scratch

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import com.cooltechworks.utils.BitmapUtils
import me.tiptap.tiptap.R
import java.util.*

@Suppress("NAME_SHADOWING")
/**
 * Created by Harish on 25/03/16.
 */
class ScratchCard : android.support.v7.widget.AppCompatTextView {

    private var mX: Float = 0.toFloat()
    private var mY: Float = 0.toFloat()
    /**
     * Bitmap holding the scratch region.
     */
    private var mScratchBitmap: Bitmap? = null

    /**
     * Drawable canvas area through which the scratchable area is drawn.
     */
    private var mCanvas: Canvas? = null

    /**
     * Path holding the erasing path done by the user.
     */
    private var mErasePath: Path? = null

    /**
     * Path to indicate where the user have touched.
     */
    private var mTouchPath: Path? = null


    /**
     * Paint properties for erasing the scratch region.
     */
    private var mErasePaint: Paint? = null

    /**
     * Gradient paint properties that lies as a background for scratch region.
     */
    private var mGradientBgPaint: Paint? = null

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
    private var mRevealPercent: Float = 0.toFloat()

    /**
     * Thread Count
     */
    private var mThreadCount = 0

    val color: Int
        get() = mErasePaint!!.color

    val isRevealed: Boolean
        get() = mRevealPercent.toDouble() == 0.5

    private val textBounds: IntArray
        get() = getTextBounds(1f)


    interface IRevealListener {
        fun onRevealed(tv: ScratchCard)
        fun onRevealPercentChangedListener(stv: ScratchCard, percent: Float)
    }


    constructor(context: Context) : super(context) {
        init()

    }

    constructor(context: Context, set: AttributeSet) : super(context, set) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    /**
     * Set the strokes width based on the parameter multiplier.
     * @param multiplier can be 1,2,3 and so on to set the stroke width of the paint.
     */
    fun setStrokeWidth(multiplier: Int) {
        mErasePaint!!.strokeWidth = multiplier * STROKE_WIDTH
    }

    /**
     * Initialises the paint drawing elements.
     */
    private fun init() {
        mTouchPath = Path()

        mErasePaint = Paint()
        mErasePaint!!.isAntiAlias = true
        mErasePaint!!.isDither = true
        mErasePaint!!.color = -0x10000
        mErasePaint!!.style = Paint.Style.STROKE
        mErasePaint!!.strokeJoin = Paint.Join.BEVEL
        mErasePaint!!.strokeCap = Paint.Cap.ROUND
        mErasePaint!!.xfermode = PorterDuffXfermode(
                PorterDuff.Mode.CLEAR)
        setStrokeWidth(20)

        mGradientBgPaint = Paint()

        mErasePath = Path()
        mBitmapPaint = Paint(Paint.DITHER_FLAG)

        val rand = Random()
        val scratchNum = (rand.nextInt(50) + 1) % 3
        when (scratchNum) {
            0 -> scratchBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_scratch_01)
            1 -> scratchBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_scratch_02)
            2 -> scratchBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_scratch_03)
        }
        mDrawable = BitmapDrawable(resources, scratchBitmap)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mScratchBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mScratchBitmap!!)

        val rect = Rect(0, 0, mScratchBitmap!!.width, mScratchBitmap!!.height)
        mDrawable!!.bounds = rect

        val startGradientColor = ContextCompat.getColor(context, R.color.scratch_start_gradient)
        val endGradientColor = ContextCompat.getColor(context, R.color.scratch_end_gradient)


        mGradientBgPaint!!.shader = LinearGradient(0f, 0f, 0f, height.toFloat(), startGradientColor, endGradientColor, Shader.TileMode.MIRROR)

        mCanvas!!.drawRect(rect, mGradientBgPaint!!)
        mDrawable!!.draw(mCanvas)
    }

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)
        canvas.drawBitmap(mScratchBitmap!!, 0f, 0f, mBitmapPaint)
        canvas.drawPath(mErasePath!!, mErasePaint!!)

    }

    private fun touch_start(x: Float, y: Float) {
        mErasePath!!.reset()
        mErasePath!!.moveTo(x, y)
        mX = x
        mY = y
    }


    private fun touch_move(x: Float, y: Float) {

        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mErasePath!!.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y

            drawPath()
        }

        mTouchPath!!.reset()
        mTouchPath!!.addCircle(mX, mY, 30f, Path.Direction.CW)

    }

    private fun drawPath() {
        mErasePath!!.lineTo(mX, mY)
        // commit the path to our offscreen
        mCanvas!!.drawPath(mErasePath!!, mErasePaint!!)
        // kill this so we don't double draw
        mTouchPath!!.reset()
        mErasePath!!.reset()
        mErasePath!!.moveTo(mX, mY)

        checkRevealed()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touch_start(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touch_move(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                drawPath()
                invalidate()
            }
            else -> {
            }
        }
        return true
    }


    fun setRevealListener(listener: IRevealListener) {
        this.mRevealListener = listener
    }

    @SuppressLint("StaticFieldLeak")
    private fun checkRevealed() {

        if (!isRevealed && mRevealListener != null) {

            val bounds = textBounds
            val left = bounds[0]
            val top = bounds[1]
            val width = bounds[2] - left
            val height = bounds[3] - top


            // Do not create multiple calls to compare.
            if (mThreadCount > 1) {
                return
            }

            mThreadCount++

            object : AsyncTask<Int, Void, Float>() {

                override fun doInBackground(vararg params: Int?): Float? {

                    try {
                        val left = params[0]
                        val top = params[1]
                        val width = params[2]
                        val height = params[3]

                        val croppedBitmap = Bitmap.createBitmap(mScratchBitmap!!, left!!, top!!, width!!, height!!)

                        return BitmapUtils.getTransparentPixelPercent(croppedBitmap)
                    } finally {
                        mThreadCount--
                        Log.d("Thread Count", Integer.toString(mThreadCount))

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

                        // if now revealed.
                        if (isRevealed) {
                            mRevealListener!!.onRevealed(this@ScratchCard)

                        }
                    }
                }
            }.execute(left, top, width, height)

        }
    }

    @SuppressLint("RtlHardcoded")
    private fun getTextBounds(scale: Float): IntArray {

        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val vwidth = width
        val vheight = height

        val centerX = vwidth / 2
        val centerY = vheight / 2


        val paint = paint

        val text = text.toString()

        val dimens = getTextDimens(text, paint)
        var width = dimens[0]
        var height = dimens[1]

        val lines = lineCount
        height *= lines
        width /= lines


        var left = 0
        var top = 0

        if (height > vheight) {
            height = vheight - (paddingBottom + paddingTop)
        } else {
            height = (height * scale).toInt()
        }

        if (width > vwidth) {
            width = vwidth - (paddingLeft + paddingRight)
        } else {
            width = (width * scale).toInt()
        }

        val gravity = gravity


        //todo Gravity.START
        if (gravity and Gravity.LEFT == Gravity.LEFT) {
            left = paddingLeft
        } else if (gravity and Gravity.RIGHT == Gravity.RIGHT) {
            left = vwidth - paddingRight - width
        } else if (gravity and Gravity.CENTER_HORIZONTAL == Gravity.CENTER_HORIZONTAL) {
            left = centerX - width / 2
        }//todo Gravity.END

        if (gravity and Gravity.TOP == Gravity.TOP) {
            top = paddingTop
        } else if (gravity and Gravity.BOTTOM == Gravity.BOTTOM) {
            top = vheight - paddingBottom - height
        } else if (gravity and Gravity.CENTER_VERTICAL == Gravity.CENTER_VERTICAL) {
            top = centerY - height / 2
        }

        return intArrayOf(left, top, left + width, top + height)
    }

    companion object {

        const val STROKE_WIDTH = 12f
        private const val TOUCH_TOLERANCE = 4f
        lateinit var scratchBitmap: Bitmap

        /**
         * Paint properties for drawing the scratch area.
         */
        lateinit var mBitmapPaint: Paint


        private fun getTextDimens(text: String, paint: Paint): IntArray {

            val end = text.length
            val bounds = Rect()
            paint.getTextBounds(text, 0, end, bounds)
            val width = bounds.left + bounds.width()
            val height = bounds.bottom + bounds.height()

            return intArrayOf(width, height)
        }
    }


}
