package me.tiptap.tiptap.scratch

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.BaseAdapter
import android.widget.TextView
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.FragmentScratchBinding


class ScratchFragment : Fragment() {

    private lateinit var binding: FragmentScratchBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scratch, container, false)

        setShareMainLayoutSize() // Change share main container's height

        binding.layoutScratchMain?.textScratchMainNum?.text = getString(R.string.count_tiptap, 0) //temp

        binding.scratch.setRevealListener(object : ScratchCard.IRevealListener {
            override fun onRevealPercentChangedListener(stv: ScratchCard?, percent: Float) {
                Log.d("ScratchPer", percent.toString())
                if (percent == 1.0f) {
                    Log.d("ScratchPer", "Done!")
                    fadeOutAnimation(binding.scratch, 300)
                }
            }

            override fun onRevealed(tv: ScratchCard) {
                Log.d("ScratchPer", "Hello SCratch")
            }

        })
        binding.listScratch.apply {
            divider = null
            dividerHeight = 0
            adapter = ListViewAdapter(context)
        }

        return binding.root

    }

    /**
     * Change Share main layout container's height to screen size.
     */
    private fun setShareMainLayoutSize() {
        val point = Point()

        activity?.windowManager?.defaultDisplay?.getSize(point)

        binding.containerScratchMain.layoutParams.height = point.y //change height
    }


    fun fadeOutAnimation(view: View, animationDuration: Long) {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.startOffset = animationDuration
        fadeOut.duration = animationDuration
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        view.startAnimation(fadeOut)
    }



    private class ListViewAdapter(context: Context?) : BaseAdapter() {
        internal var sList = arrayOf("#1", "키오스크 카페", "오늘 날씨는 하루종일 맑음. 어제도 오늘도 너무 더워서 아무 생각이 들지 않는다.숙소에서 나와 가장 먼저 들른 곳!")


        val mInflator: LayoutInflater = LayoutInflater.from(context)


        override fun getItem(position: Int): Any {
            return sList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return sList.size
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view: View?
            val vh: ListRowHolder
            if (convertView == null) {
                view = this.mInflator.inflate(R.layout.share_list_row, parent, false)
                vh = ListRowHolder(view)
                view!!.tag = vh
            } else {
                view = convertView
                vh = view.tag as ListRowHolder
            }

            vh.label.text = sList[position]
            return view
        }
    }

    private class ListRowHolder(row: View?) {
        public val label: TextView = row?.findViewById(R.id.label) as TextView
    }

}






